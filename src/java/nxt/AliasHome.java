/*
 * Copyright © 2013-2016 The Nxt Core Developers.
 * Copyright © 2016 Jelurida IP B.V.
 *
 * See the LICENSE.txt file at the top-level directory of this distribution
 * for licensing information.
 *
 * Unless otherwise agreed in a custom licensing agreement with Jelurida B.V.,
 * no part of the Nxt software, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE.txt file.
 *
 * Removal or modification of this copyright notice is prohibited.
 *
 */

package nxt;

import nxt.db.DbClause;
import nxt.db.DbIterator;
import nxt.db.DbKey;
import nxt.db.DbUtils;
import nxt.db.VersionedEntityDbTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class AliasHome {

    static AliasHome forChain(ChildChain childChain) {
        if (childChain.getAliasHome() != null) {
            throw new IllegalStateException("already set");
        }
        return new AliasHome(childChain);
    }

    private final DbKey.LongKeyFactory<Alias> aliasDbKeyFactory;
    private final VersionedEntityDbTable<Alias> aliasTable;
    private final DbKey.LongKeyFactory<Offer> offerDbKeyFactory;
    private final VersionedEntityDbTable<Offer> offerTable;
    private final ChildChain childChain;

    private AliasHome(ChildChain childChain) {
        this.childChain = childChain;
        this.aliasDbKeyFactory = new DbKey.LongKeyFactory<Alias>("id") {
            @Override
            public DbKey newKey(Alias alias) {
                return alias.dbKey;
            }
        };
        this.aliasTable = new VersionedEntityDbTable<Alias>(childChain.getSchemaTable("alias"), aliasDbKeyFactory) {
            @Override
            protected Alias load(Connection con, ResultSet rs, DbKey dbKey) throws SQLException {
                return new Alias(rs, dbKey);
            }
            @Override
            protected void save(Connection con, Alias alias) throws SQLException {
                alias.save(con);
            }
            @Override
            protected String defaultSort() {
                return " ORDER BY alias_name_lower ";
            }
        };
        this.offerDbKeyFactory = new DbKey.LongKeyFactory<Offer>("id") {
            @Override
            public DbKey newKey(Offer offer) {
                return offer.dbKey;
            }
        };
        this.offerTable = new VersionedEntityDbTable<Offer>(childChain.getSchemaTable("alias_offer"), offerDbKeyFactory) {
            @Override
            protected Offer load(Connection con, ResultSet rs, DbKey dbKey) throws SQLException {
                return new Offer(rs, dbKey);
            }
            @Override
            protected void save(Connection con, Offer offer) throws SQLException {
                offer.save(con);
            }
        };
    }

    public int getCount() {
        return aliasTable.getCount();
    }

    public int getAccountAliasCount(long accountId) {
        return aliasTable.getCount(new DbClause.LongClause("account_id", accountId));
    }

    public DbIterator<Alias> getAliasesByOwner(long accountId, int from, int to) {
        return aliasTable.getManyBy(new DbClause.LongClause("account_id", accountId), from, to);
    }

    public Alias getAlias(String aliasName) {
        return aliasTable.getBy(new DbClause.StringClause("alias_name_lower", aliasName.toLowerCase()));
    }

    public DbIterator<Alias> getAliasesLike(String aliasName, int from, int to) {
        return aliasTable.getManyBy(new DbClause.LikeClause("alias_name_lower", aliasName.toLowerCase()), from, to);
    }

    public Alias getAlias(long id) {
        return aliasTable.get(aliasDbKeyFactory.newKey(id));
    }

    public Offer getOffer(Alias alias) {
        return offerTable.get(offerDbKeyFactory.newKey(alias.getId()));
    }

    void deleteAlias(final String aliasName) {
        final Alias alias = getAlias(aliasName);
        final Offer offer = getOffer(alias);
        if (offer != null) {
            offerTable.delete(offer);
        }
        aliasTable.delete(alias);
    }

    void addOrUpdateAlias(Transaction transaction, Attachment.MessagingAliasAssignment attachment) {
        Alias alias = getAlias(attachment.getAliasName());
        if (alias == null) {
            alias = new Alias(transaction, attachment);
        } else {
            alias.accountId = transaction.getSenderId();
            alias.aliasURI = attachment.getAliasURI();
            alias.timestamp = Nxt.getBlockchain().getLastBlockTimestamp();
        }
        aliasTable.insert(alias);
    }

    void sellAlias(Transaction transaction, Attachment.MessagingAliasSell attachment) {
        final String aliasName = attachment.getAliasName();
        final long priceNQT = attachment.getPriceNQT();
        final long buyerId = transaction.getRecipientId();
        if (priceNQT > 0) {
            Alias alias = getAlias(aliasName);
            Offer offer = getOffer(alias);
            if (offer == null) {
                offerTable.insert(new Offer(alias.id, priceNQT, buyerId));
            } else {
                offer.priceNQT = priceNQT;
                offer.buyerId = buyerId;
                offerTable.insert(offer);
            }
        } else {
            changeOwner(buyerId, aliasName);
        }

    }

    void changeOwner(long newOwnerId, String aliasName) {
        Alias alias = getAlias(aliasName);
        alias.accountId = newOwnerId;
        alias.timestamp = Nxt.getBlockchain().getLastBlockTimestamp();
        aliasTable.insert(alias);
        Offer offer = getOffer(alias);
        offerTable.delete(offer);
    }


    public final class Offer {

        private long priceNQT;
        private long buyerId;
        private final long aliasId;
        private final DbKey dbKey;

        private Offer(long aliasId, long priceNQT, long buyerId) {
            this.priceNQT = priceNQT;
            this.buyerId = buyerId;
            this.aliasId = aliasId;
            this.dbKey = offerDbKeyFactory.newKey(this.aliasId);
        }

        private Offer(ResultSet rs, DbKey dbKey) throws SQLException {
            this.aliasId = rs.getLong("id");
            this.dbKey = dbKey;
            this.priceNQT = rs.getLong("price");
            this.buyerId = rs.getLong("buyer_id");
        }

        private void save(Connection con) throws SQLException {
            try (PreparedStatement pstmt = con.prepareStatement("INSERT INTO alias_offer (id, price, buyer_id, "
                    + "height) VALUES (?, ?, ?, ?)")) {
                int i = 0;
                pstmt.setLong(++i, this.aliasId);
                pstmt.setLong(++i, this.priceNQT);
                DbUtils.setLongZeroToNull(pstmt, ++i, this.buyerId);
                pstmt.setInt(++i, Nxt.getBlockchain().getHeight());
                pstmt.executeUpdate();
            }
        }

        public long getId() {
            return aliasId;
        }

        public long getPriceNQT() {
            return priceNQT;
        }

        public long getBuyerId() {
            return buyerId;
        }

    }

    public final class Alias {

        private long accountId;
        private final long id;
        private final DbKey dbKey;
        private final String aliasName;
        private String aliasURI;
        private int timestamp;

        private Alias(Transaction transaction, Attachment.MessagingAliasAssignment attachment) {
            this.id = transaction.getId();
            this.dbKey = aliasDbKeyFactory.newKey(this.id);
            this.accountId = transaction.getSenderId();
            this.aliasName = attachment.getAliasName();
            this.aliasURI = attachment.getAliasURI();
            this.timestamp = Nxt.getBlockchain().getLastBlockTimestamp();
        }

        private Alias(ResultSet rs, DbKey dbKey) throws SQLException {
            this.id = rs.getLong("id");
            this.dbKey = dbKey;
            this.accountId = rs.getLong("account_id");
            this.aliasName = rs.getString("alias_name");
            this.aliasURI = rs.getString("alias_uri");
            this.timestamp = rs.getInt("timestamp");
        }

        private void save(Connection con) throws SQLException {
            try (PreparedStatement pstmt = con.prepareStatement("INSERT INTO alias (id, account_id, alias_name, "
                    + "alias_uri, timestamp, height) "
                    + "VALUES (?, ?, ?, ?, ?, ?)")) {
                int i = 0;
                pstmt.setLong(++i, this.id);
                pstmt.setLong(++i, this.accountId);
                pstmt.setString(++i, this.aliasName);
                pstmt.setString(++i, this.aliasURI);
                pstmt.setInt(++i, this.timestamp);
                pstmt.setInt(++i, Nxt.getBlockchain().getHeight());
                pstmt.executeUpdate();
            }
        }

        public long getId() {
            return id;
        }

        public String getAliasName() {
            return aliasName;
        }

        public String getAliasURI() {
            return aliasURI;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public long getAccountId() {
            return accountId;
        }

        public Offer getOffer() {
            return AliasHome.this.getOffer(this);
        }

    }

}