/******************************************************************************
 * Copyright © 2013-2016 The Nxt Core Developers.                             *
 *                                                                            *
 * See the AUTHORS.txt, DEVELOPER-AGREEMENT.txt and LICENSE.txt files at      *
 * the top-level directory of this distribution for the individual copyright  *
 * holder information and the developer policies on copyright and licensing.  *
 *                                                                            *
 * Unless otherwise agreed in a custom licensing agreement, no part of the    *
 * Nxt software, including this file, may be copied, modified, propagated,    *
 * or distributed except according to the terms contained in the LICENSE.txt  *
 * file.                                                                      *
 *                                                                            *
 * Removal or modification of this copyright notice is prohibited.            *
 *                                                                            *
 ******************************************************************************/

package nxt;

import nxt.db.DbVersion;

class ChildDbVersion extends DbVersion {

    ChildDbVersion(String schema) {
        super(schema);
    }

    protected void update(int nextUpdate) {
        switch (nextUpdate) {
            case 1:
                apply("CREATE TABLE IF NOT EXISTS transaction (db_id IDENTITY, id BIGINT NOT NULL, "
                        + "deadline SMALLINT NOT NULL, recipient_id BIGINT, transaction_index SMALLINT NOT NULL, "
                        + "amount BIGINT NOT NULL, fee BIGINT NOT NULL, full_hash BINARY(32) NOT NULL, "
                        + "height INT NOT NULL, block_id BIGINT NOT NULL, FOREIGN KEY (block_id) REFERENCES PUBLIC.block (id) ON DELETE CASCADE, "
                        + "signature BINARY(64) NOT NULL, timestamp INT NOT NULL, type TINYINT NOT NULL, subtype TINYINT NOT NULL, "
                        + "sender_id BIGINT NOT NULL, block_timestamp INT NOT NULL, referenced_transaction_full_hash BINARY(32), "
                        + "phased BOOLEAN NOT NULL DEFAULT FALSE, "
                        + "attachment_bytes VARBINARY, version TINYINT NOT NULL, has_message BOOLEAN NOT NULL DEFAULT FALSE, "
                        + "has_encrypted_message BOOLEAN NOT NULL DEFAULT FALSE, has_public_key_announcement BOOLEAN NOT NULL DEFAULT FALSE, "
                        + "ec_block_height INT DEFAULT NULL, ec_block_id BIGINT DEFAULT NULL, has_encrypttoself_message BOOLEAN NOT NULL DEFAULT FALSE, "
                        + "has_prunable_message BOOLEAN NOT NULL DEFAULT FALSE, has_prunable_encrypted_message BOOLEAN NOT NULL DEFAULT FALSE, "
                        + "has_prunable_attachment BOOLEAN NOT NULL DEFAULT FALSE)");
            case 2:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS transaction_id_idx ON transaction (id)");
            case 3:
                apply("CREATE INDEX IF NOT EXISTS transaction_sender_id_idx ON transaction (sender_id)");
            case 4:
                apply("CREATE INDEX IF NOT EXISTS transaction_recipient_id_idx ON transaction (recipient_id)");
            case 5:
                apply("CREATE INDEX IF NOT EXISTS transaction_block_timestamp_idx ON transaction (block_timestamp DESC)");
            case 6:
                apply("CREATE TABLE IF NOT EXISTS alias (db_id IDENTITY, id BIGINT NOT NULL, "
                        + "account_id BIGINT NOT NULL, alias_name VARCHAR NOT NULL, "
                        + "alias_name_lower VARCHAR AS LOWER (alias_name) NOT NULL, "
                        + "alias_uri VARCHAR NOT NULL, timestamp INT NOT NULL, "
                        + "height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 7:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS alias_id_height_idx ON alias (id, height DESC)");
            case 8:
                apply("CREATE INDEX IF NOT EXISTS alias_account_id_idx ON alias (account_id, height DESC)");
            case 9:
                apply("CREATE INDEX IF NOT EXISTS alias_name_lower_idx ON alias (alias_name_lower)");
            case 10:
                apply("CREATE TABLE IF NOT EXISTS alias_offer (db_id IDENTITY, id BIGINT NOT NULL, "
                        + "price BIGINT NOT NULL, buyer_id BIGINT, "
                        + "height INT NOT NULL, latest BOOLEAN DEFAULT TRUE NOT NULL)");
            case 11:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS alias_offer_id_height_idx ON alias_offer (id, height DESC)");
            case 12:
                apply("CREATE TABLE IF NOT EXISTS asset (db_id IDENTITY, id BIGINT NOT NULL, account_id BIGINT NOT NULL, "
                        + "name VARCHAR NOT NULL, description VARCHAR, quantity BIGINT NOT NULL, decimals TINYINT NOT NULL, "
                        + "initial_quantity BIGINT NOT NULL, height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 13:
                apply("CREATE INDEX IF NOT EXISTS asset_account_id_idx ON asset (account_id)");
            case 14:
                apply("CREATE TABLE IF NOT EXISTS trade (db_id IDENTITY, asset_id BIGINT NOT NULL, block_id BIGINT NOT NULL, "
                        + "ask_order_id BIGINT NOT NULL, bid_order_id BIGINT NOT NULL, ask_order_height INT NOT NULL, "
                        + "bid_order_height INT NOT NULL, seller_id BIGINT NOT NULL, buyer_id BIGINT NOT NULL, "
                        + "is_buy BOOLEAN NOT NULL, "
                        + "quantity BIGINT NOT NULL, price BIGINT NOT NULL, timestamp INT NOT NULL, height INT NOT NULL)");
            case 15:
                apply("CREATE INDEX IF NOT EXISTS trade_asset_id_idx ON trade (asset_id, height DESC)");
            case 16:
                apply("CREATE INDEX IF NOT EXISTS trade_seller_id_idx ON trade (seller_id, height DESC)");
            case 17:
                apply("CREATE INDEX IF NOT EXISTS trade_buyer_id_idx ON trade (buyer_id, height DESC)");
            case 18:
                apply("CREATE TABLE IF NOT EXISTS ask_order (db_id IDENTITY, id BIGINT NOT NULL, account_id BIGINT NOT NULL, "
                        + "asset_id BIGINT NOT NULL, price BIGINT NOT NULL, transaction_index SMALLINT NOT NULL, "
                        + "transaction_height INT NOT NULL, "
                        + "quantity BIGINT NOT NULL, creation_height INT NOT NULL, height INT NOT NULL, "
                        + "latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 19:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS ask_order_id_height_idx ON ask_order (id, height DESC)");
            case 20:
                apply("CREATE INDEX IF NOT EXISTS ask_order_account_id_idx ON ask_order (account_id, height DESC)");
            case 21:
                apply("CREATE INDEX IF NOT EXISTS ask_order_asset_id_price_idx ON ask_order (asset_id, price)");
            case 22:
                apply("CREATE TABLE IF NOT EXISTS bid_order (db_id IDENTITY, id BIGINT NOT NULL, account_id BIGINT NOT NULL, "
                        + "asset_id BIGINT NOT NULL, price BIGINT NOT NULL, transaction_index SMALLINT NOT NULL, "
                        + "transaction_height INT NOT NULL, "
                        + "quantity BIGINT NOT NULL, creation_height INT NOT NULL, height INT NOT NULL, "
                        + "latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 23:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS bid_order_id_height_idx ON bid_order (id, height DESC)");
            case 24:
                apply("CREATE INDEX IF NOT EXISTS bid_order_account_id_idx ON bid_order (account_id, height DESC)");
            case 25:
                apply("CREATE INDEX IF NOT EXISTS bid_order_asset_id_price_idx ON bid_order (asset_id, price DESC)");
            case 26:
                apply("CREATE TABLE IF NOT EXISTS goods (db_id IDENTITY, id BIGINT NOT NULL, seller_id BIGINT NOT NULL, "
                        + "name VARCHAR NOT NULL, description VARCHAR, parsed_tags ARRAY, "
                        + "tags VARCHAR, timestamp INT NOT NULL, quantity INT NOT NULL, price BIGINT NOT NULL, "
                        + "delisted BOOLEAN NOT NULL, height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 27:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS goods_id_height_idx ON goods (id, height DESC)");
            case 28:
                apply("CREATE INDEX IF NOT EXISTS goods_seller_id_name_idx ON goods (seller_id, name)");
            case 29:
                apply("CREATE INDEX IF NOT EXISTS goods_timestamp_idx ON goods (timestamp DESC, height DESC)");
            case 30:
                apply("CREATE TABLE IF NOT EXISTS purchase (db_id IDENTITY, id BIGINT NOT NULL, buyer_id BIGINT NOT NULL, "
                        + "goods_id BIGINT NOT NULL, "
                        + "seller_id BIGINT NOT NULL, quantity INT NOT NULL, "
                        + "price BIGINT NOT NULL, deadline INT NOT NULL, note VARBINARY, nonce BINARY(32), "
                        + "timestamp INT NOT NULL, pending BOOLEAN NOT NULL, goods VARBINARY, goods_nonce BINARY(32), "
                        + "refund_note VARBINARY, refund_nonce BINARY(32), has_feedback_notes BOOLEAN NOT NULL DEFAULT FALSE, "
                        + "has_public_feedbacks BOOLEAN NOT NULL DEFAULT FALSE, discount BIGINT NOT NULL, refund BIGINT NOT NULL, "
                        + "height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 31:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS purchase_id_height_idx ON purchase (id, height DESC)");
            case 32:
                apply("CREATE INDEX IF NOT EXISTS purchase_buyer_id_height_idx ON purchase (buyer_id, height DESC)");
            case 33:
                apply("CREATE INDEX IF NOT EXISTS purchase_seller_id_height_idx ON purchase (seller_id, height DESC)");
            case 34:
                apply("CREATE INDEX IF NOT EXISTS purchase_deadline_idx ON purchase (deadline DESC, height DESC)");
            case 35:
                apply("CREATE TABLE IF NOT EXISTS account (db_id IDENTITY, id BIGINT NOT NULL, "
                        + "balance BIGINT NOT NULL, unconfirmed_balance BIGINT NOT NULL, has_control_phasing BOOLEAN NOT NULL DEFAULT FALSE, "
                        + "forged_balance BIGINT NOT NULL, active_lessee_id BIGINT, height INT NOT NULL, "
                        + "latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 36:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS account_id_height_idx ON account (id, height DESC)");
            case 37:
                apply("CREATE TABLE IF NOT EXISTS purchase_feedback (db_id IDENTITY, id BIGINT NOT NULL, feedback_data VARBINARY NOT NULL, "
                        + "feedback_nonce BINARY(32) NOT NULL, height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 38:
                apply("CREATE INDEX IF NOT EXISTS purchase_feedback_id_height_idx ON purchase_feedback (id, height DESC)");
            case 39:
                apply("CREATE TABLE IF NOT EXISTS purchase_public_feedback (db_id IDENTITY, id BIGINT NOT NULL, public_feedback "
                        + "VARCHAR NOT NULL, height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 40:
                apply("CREATE INDEX IF NOT EXISTS purchase_public_feedback_id_height_idx ON purchase_public_feedback (id, height DESC)");
            case 41:
                apply("CREATE TABLE IF NOT EXISTS asset_transfer (db_id IDENTITY, id BIGINT NOT NULL, asset_id BIGINT NOT NULL, "
                        + "sender_id BIGINT NOT NULL, recipient_id BIGINT NOT NULL, quantity BIGINT NOT NULL, timestamp INT NOT NULL, "
                        + "height INT NOT NULL)");
            case 42:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS asset_transfer_id_idx ON asset_transfer (id)");
            case 43:
                apply("CREATE INDEX IF NOT EXISTS asset_transfer_asset_id_idx ON asset_transfer (asset_id, height DESC)");
            case 44:
                apply("CREATE INDEX IF NOT EXISTS asset_transfer_sender_id_idx ON asset_transfer (sender_id, height DESC)");
            case 45:
                apply("CREATE INDEX IF NOT EXISTS asset_transfer_recipient_id_idx ON asset_transfer (recipient_id, height DESC)");
            case 46:
                apply("CREATE INDEX IF NOT EXISTS purchase_timestamp_idx ON purchase (timestamp DESC, id)");
            case 47:
                apply("CREATE INDEX IF NOT EXISTS ask_order_creation_idx ON ask_order (creation_height DESC)");
            case 48:
                apply("CREATE INDEX IF NOT EXISTS bid_order_creation_idx ON bid_order (creation_height DESC)");
            case 49:
                apply("CREATE TABLE IF NOT EXISTS tag (db_id IDENTITY, tag VARCHAR NOT NULL, in_stock_count INT NOT NULL, "
                        + "total_count INT NOT NULL, height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 50:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS tag_tag_idx ON tag (tag, height DESC)");
            case 51:
                apply("CREATE INDEX IF NOT EXISTS tag_in_stock_count_idx ON tag (in_stock_count DESC, height DESC)");
            case 52:
                apply("CREATE TABLE IF NOT EXISTS currency (db_id IDENTITY, id BIGINT NOT NULL, account_id BIGINT NOT NULL, "
                        + "name VARCHAR NOT NULL, name_lower VARCHAR AS LOWER (name) NOT NULL, code VARCHAR NOT NULL, "
                        + "description VARCHAR, type INT NOT NULL, initial_supply BIGINT NOT NULL DEFAULT 0, "
                        + "reserve_supply BIGINT NOT NULL, max_supply BIGINT NOT NULL, creation_height INT NOT NULL, issuance_height INT NOT NULL, "
                        + "min_reserve_per_unit_nqt BIGINT NOT NULL, min_difficulty TINYINT NOT NULL, "
                        + "max_difficulty TINYINT NOT NULL, ruleset TINYINT NOT NULL, algorithm TINYINT NOT NULL, "
                        + "decimals TINYINT NOT NULL DEFAULT 0,"
                        + "height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 53:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS currency_id_height_idx ON currency (id, height DESC)");
            case 54:
                apply("CREATE INDEX IF NOT EXISTS currency_account_id_idx ON currency (account_id)");
            case 55:
                apply("CREATE TABLE IF NOT EXISTS currency_founder (db_id IDENTITY, currency_id BIGINT NOT NULL, "
                        + "account_id BIGINT NOT NULL, amount BIGINT NOT NULL, "
                        + "height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 56:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS currency_founder_currency_id_idx ON currency_founder (currency_id, account_id, height DESC)");
            case 57:
                apply("CREATE TABLE IF NOT EXISTS currency_mint (db_id IDENTITY, currency_id BIGINT NOT NULL, account_id BIGINT NOT NULL, "
                        + "counter BIGINT NOT NULL, height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 58:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS currency_mint_currency_id_account_id_idx ON currency_mint (currency_id, account_id, height DESC)");
            case 59:
                apply("CREATE TABLE IF NOT EXISTS buy_offer (db_id IDENTITY, id BIGINT NOT NULL, currency_id BIGINT NOT NULL, account_id BIGINT NOT NULL,"
                        + "rate BIGINT NOT NULL, unit_limit BIGINT NOT NULL, supply BIGINT NOT NULL, expiration_height INT NOT NULL,"
                        + "creation_height INT NOT NULL, transaction_index SMALLINT NOT NULL, transaction_height INT NOT NULL, height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 60:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS buy_offer_id_idx ON buy_offer (id, height DESC)");
            case 61:
                apply("CREATE INDEX IF NOT EXISTS buy_offer_currency_id_account_id_idx ON buy_offer (currency_id, account_id, height DESC)");
            case 62:
                apply("CREATE TABLE IF NOT EXISTS sell_offer (db_id IDENTITY, id BIGINT NOT NULL, currency_id BIGINT NOT NULL, account_id BIGINT NOT NULL, "
                        + "rate BIGINT NOT NULL, unit_limit BIGINT NOT NULL, supply BIGINT NOT NULL, expiration_height INT NOT NULL, "
                        + "creation_height INT NOT NULL, transaction_index SMALLINT NOT NULL, transaction_height INT NOT NULL, height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 63:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS sell_offer_id_idx ON sell_offer (id, height DESC)");
            case 64:
                apply("CREATE INDEX IF NOT EXISTS sell_offer_currency_id_account_id_idx ON sell_offer (currency_id, account_id, height DESC)");
            case 65:
                apply("CREATE TABLE IF NOT EXISTS exchange (db_id IDENTITY, transaction_id BIGINT NOT NULL, currency_id BIGINT NOT NULL, block_id BIGINT NOT NULL, "
                        + "offer_id BIGINT NOT NULL, seller_id BIGINT NOT NULL, "
                        + "buyer_id BIGINT NOT NULL, units BIGINT NOT NULL, "
                        + "rate BIGINT NOT NULL, timestamp INT NOT NULL, height INT NOT NULL)");
            case 66:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS exchange_offer_idx ON exchange (transaction_id, offer_id)");
            case 67:
                apply("CREATE INDEX IF NOT EXISTS exchange_currency_id_idx ON exchange (currency_id, height DESC)");
            case 68:
                apply("CREATE INDEX IF NOT EXISTS exchange_seller_id_idx ON exchange (seller_id, height DESC)");
            case 69:
                apply("CREATE INDEX IF NOT EXISTS exchange_buyer_id_idx ON exchange (buyer_id, height DESC)");
            case 70:
                apply("CREATE TABLE IF NOT EXISTS currency_transfer (db_id IDENTITY, id BIGINT NOT NULL, currency_id BIGINT NOT NULL, "
                        + "sender_id BIGINT NOT NULL, recipient_id BIGINT NOT NULL, units BIGINT NOT NULL, timestamp INT NOT NULL, "
                        + "height INT NOT NULL)");
            case 71:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS currency_transfer_id_idx ON currency_transfer (id)");
            case 72:
                apply("CREATE INDEX IF NOT EXISTS currency_transfer_currency_id_idx ON currency_transfer (currency_id, height DESC)");
            case 73:
                apply("CREATE INDEX IF NOT EXISTS currency_transfer_sender_id_idx ON currency_transfer (sender_id, height DESC)");
            case 74:
                apply("CREATE INDEX IF NOT EXISTS currency_transfer_recipient_id_idx ON currency_transfer (recipient_id, height DESC)");
            case 75:
                apply("CREATE INDEX IF NOT EXISTS currency_name_idx ON currency (name_lower, height DESC)");
            case 76:
                apply("CREATE INDEX IF NOT EXISTS currency_code_idx ON currency (code, height DESC)");
            case 77:
                apply("CREATE INDEX IF NOT EXISTS buy_offer_rate_height_idx ON buy_offer (rate DESC, creation_height ASC)");
            case 78:
                apply("CREATE INDEX IF NOT EXISTS sell_offer_rate_height_idx ON sell_offer (rate ASC, creation_height ASC)");
            case 79:
                apply("CREATE INDEX IF NOT EXISTS currency_creation_height_idx ON currency (creation_height DESC)");
            case 80:
                apply("CREATE TABLE IF NOT EXISTS currency_supply (db_id IDENTITY, id BIGINT NOT NULL, "
                        + "current_supply BIGINT NOT NULL, current_reserve_per_unit_nqt BIGINT NOT NULL, height INT NOT NULL, "
                        + "latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 81:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS currency_supply_id_height_idx ON currency_supply (id, height DESC)");
            case 82:
                apply("CREATE INDEX IF NOT EXISTS asset_transfer_height_idx ON asset_transfer(height)");
            case 83:
                apply("CREATE INDEX IF NOT EXISTS currency_transfer_height_idx ON currency_transfer(height)");
            case 84:
                apply("CREATE INDEX IF NOT EXISTS exchange_height_idx ON exchange(height)");
            case 85:
                apply("CREATE INDEX IF NOT EXISTS trade_height_idx ON trade(height)");
            case 86:
                apply("CREATE TABLE IF NOT EXISTS vote (db_id IDENTITY, id BIGINT NOT NULL, " +
                        "poll_id BIGINT NOT NULL, voter_id BIGINT NOT NULL, vote_bytes VARBINARY NOT NULL, height INT NOT NULL)");
            case 87:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS vote_id_idx ON vote (id)");
            case 88:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS vote_poll_id_idx ON vote (poll_id, voter_id)");
            case 89:
                apply("CREATE TABLE IF NOT EXISTS poll (db_id IDENTITY, id BIGINT NOT NULL, "
                        + "account_id BIGINT NOT NULL, name VARCHAR NOT NULL, "
                        + "description VARCHAR, options ARRAY NOT NULL, min_num_options TINYINT, max_num_options TINYINT, "
                        + "min_range_value TINYINT, max_range_value TINYINT, timestamp INT NOT NULL, "
                        + "finish_height INT NOT NULL, voting_model TINYINT NOT NULL, min_balance BIGINT, "
                        + "min_balance_model TINYINT, holding_id BIGINT, height INT NOT NULL)");
            case 90:
                apply("CREATE TABLE IF NOT EXISTS poll_result (db_id IDENTITY, poll_id BIGINT NOT NULL, "
                        + "result BIGINT, weight BIGINT NOT NULL, height INT NOT NULL)");
            case 91:
                apply("CREATE TABLE IF NOT EXISTS phasing_poll (db_id IDENTITY, id BIGINT NOT NULL, "
                        + "account_id BIGINT NOT NULL, whitelist_size TINYINT NOT NULL DEFAULT 0, "
                        + "finish_height INT NOT NULL, voting_model TINYINT NOT NULL, quorum BIGINT, "
                        + "min_balance BIGINT, holding_id BIGINT, min_balance_model TINYINT, "
                        + "hashed_secret VARBINARY, algorithm TINYINT, height INT NOT NULL)");
            case 92:
                apply("CREATE TABLE IF NOT EXISTS phasing_vote (db_id IDENTITY, vote_id BIGINT NOT NULL, "
                        + "transaction_id BIGINT NOT NULL, voter_id BIGINT NOT NULL, "
                        + "height INT NOT NULL)");
            case 93:
                apply("CREATE TABLE IF NOT EXISTS phasing_poll_voter (db_id IDENTITY, "
                        + "transaction_id BIGINT NOT NULL, voter_id BIGINT NOT NULL, "
                        + "height INT NOT NULL)");
            case 94:
                apply("CREATE INDEX IF NOT EXISTS vote_height_idx ON vote(height)");
            case 95:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS poll_id_idx ON poll(id)");
            case 96:
                apply("CREATE INDEX IF NOT EXISTS poll_height_idx ON poll(height)");
            case 97:
                apply("CREATE INDEX IF NOT EXISTS poll_account_idx ON poll(account_id)");
            case 98:
                apply("CREATE INDEX IF NOT EXISTS poll_finish_height_idx ON poll(finish_height DESC)");
            case 99:
                apply("CREATE INDEX IF NOT EXISTS poll_result_poll_id_idx ON poll_result(poll_id)");
            case 100:
                apply("CREATE INDEX IF NOT EXISTS poll_result_height_idx ON poll_result(height)");
            case 101:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS phasing_poll_id_idx ON phasing_poll(id)");
            case 102:
                apply("CREATE INDEX IF NOT EXISTS phasing_poll_height_idx ON phasing_poll(height)");
            case 103:
                apply("CREATE INDEX IF NOT EXISTS phasing_poll_account_id_idx ON phasing_poll(account_id, height DESC)");
            case 104:
                apply("CREATE INDEX IF NOT EXISTS phasing_poll_holding_id_idx ON phasing_poll(holding_id, height DESC)");
            case 105:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS phasing_vote_transaction_voter_idx ON phasing_vote(transaction_id, voter_id)");
            case 106:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS phasing_poll_voter_transaction_voter_idx ON phasing_poll_voter(transaction_id, voter_id)");
            case 107:
                apply("CREATE TABLE IF NOT EXISTS phasing_poll_result (db_id IDENTITY, id BIGINT NOT NULL, "
                        + "result BIGINT NOT NULL, approved BOOLEAN NOT NULL, height INT NOT NULL)");
            case 108:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS phasing_poll_result_id_idx ON phasing_poll_result(id)");
            case 109:
                apply("CREATE INDEX IF NOT EXISTS phasing_poll_result_height_idx ON phasing_poll_result(height)");
            case 110:
                apply("CREATE INDEX IF NOT EXISTS currency_founder_account_id_idx ON currency_founder (account_id, height DESC)");
            case 111:
                apply("CREATE INDEX IF NOT EXISTS phasing_poll_voter_height_idx ON phasing_poll_voter(height)");
            case 112:
                apply("CREATE INDEX IF NOT EXISTS phasing_vote_height_idx ON phasing_vote(height)");
            case 113:
                apply("CREATE INDEX IF NOT EXISTS trade_ask_idx ON trade (ask_order_id, height DESC)");
            case 114:
                apply("CREATE INDEX IF NOT EXISTS trade_bid_idx ON trade (bid_order_id, height DESC)");
            case 115:
                apply("CREATE TABLE IF NOT EXISTS prunable_message (db_id IDENTITY, id BIGINT NOT NULL, sender_id BIGINT NOT NULL, "
                        + "recipient_id BIGINT, message VARBINARY, message_is_text BOOLEAN NOT NULL, is_compressed BOOLEAN NOT NULL, "
                        + "encrypted_message VARBINARY, encrypted_is_text BOOLEAN DEFAULT FALSE, "
                        + "block_timestamp INT NOT NULL, transaction_timestamp INT NOT NULL, height INT NOT NULL, "
                        + "FOREIGN KEY (height) REFERENCES PUBLIC.block (height) ON DELETE CASCADE)");
            case 116:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS prunable_message_id_idx ON prunable_message (id)");
            case 117:
                apply("CREATE INDEX IF NOT EXISTS prunable_message_transaction_timestamp_idx ON prunable_message (transaction_timestamp DESC)");
            case 118:
                apply("CREATE INDEX IF NOT EXISTS prunable_message_sender_idx ON prunable_message (sender_id)");
            case 119:
                apply("CREATE INDEX IF NOT EXISTS prunable_message_recipient_idx ON prunable_message (recipient_id)");
            case 120:
                apply("CREATE INDEX IF NOT EXISTS prunable_message_block_timestamp_dbid_idx ON prunable_message (block_timestamp DESC, db_id DESC)");
            case 121:
                apply("CREATE TABLE IF NOT EXISTS tagged_data (db_id IDENTITY, id BIGINT NOT NULL, account_id BIGINT NOT NULL, "
                        + "name VARCHAR NOT NULL, description VARCHAR, tags VARCHAR, parsed_tags ARRAY, type VARCHAR, data VARBINARY NOT NULL, "
                        + "is_text BOOLEAN NOT NULL, channel VARCHAR, filename VARCHAR, block_timestamp INT NOT NULL, transaction_timestamp INT NOT NULL, "
                        + "height INT NOT NULL, FOREIGN KEY (height) REFERENCES PUBLIC.block (height) ON DELETE CASCADE, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 122:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS tagged_data_id_height_idx ON tagged_data (id, height DESC)");
            case 123:
                apply("CREATE INDEX IF NOT EXISTS tagged_data_expiration_idx ON tagged_data (transaction_timestamp DESC)");
            case 124:
                apply("CREATE INDEX IF NOT EXISTS tagged_data_account_id_height_idx ON tagged_data (account_id, height DESC)");
            case 125:
                apply("CREATE INDEX IF NOT EXISTS tagged_data_block_timestamp_height_db_id_idx ON tagged_data (block_timestamp DESC, height DESC, db_id DESC)");
            case 126:
                apply("CREATE TABLE IF NOT EXISTS data_tag (db_id IDENTITY, tag VARCHAR NOT NULL, tag_count INT NOT NULL, "
                        + "height INT NOT NULL, FOREIGN KEY (height) REFERENCES PUBLIC.block (height) ON DELETE CASCADE, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 127:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS data_tag_tag_height_idx ON data_tag (tag, height DESC)");
            case 128:
                apply("CREATE INDEX IF NOT EXISTS data_tag_count_height_idx ON data_tag (tag_count DESC, height DESC)");
            case 129:
                apply("CREATE TABLE IF NOT EXISTS tagged_data_timestamp (db_id IDENTITY, id BIGINT NOT NULL, timestamp INT NOT NULL, "
                        + "height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 130:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS tagged_data_timestamp_id_height_idx ON tagged_data_timestamp (id, height DESC)");
            case 131:
                apply("CREATE INDEX IF NOT EXISTS tagged_data_channel_idx ON tagged_data (channel, height DESC)");
            case 132:
                apply("CREATE INDEX IF NOT EXISTS currency_issuance_height_idx ON currency (issuance_height)");
            case 133:
                apply("CREATE INDEX IF NOT EXISTS account_height_id_idx ON account (height, id)");
            case 134:
                apply("CREATE INDEX IF NOT EXISTS alias_height_id_idx ON alias (height, id)");
            case 135:
                apply("CREATE INDEX IF NOT EXISTS alias_offer_height_id_idx ON alias_offer (height, id)");
            case 136:
                apply("CREATE INDEX IF NOT EXISTS ask_order_height_id_idx ON ask_order (height, id)");
            case 137:
                apply("CREATE INDEX IF NOT EXISTS bid_order_height_id_idx ON bid_order (height, id)");
            case 138:
                apply("CREATE INDEX IF NOT EXISTS buy_offer_height_id_idx ON buy_offer (height, id)");
            case 139:
                apply("CREATE INDEX IF NOT EXISTS currency_height_id_idx ON currency (height, id)");
            case 140:
                apply("CREATE INDEX IF NOT EXISTS currency_founder_height_id_idx ON currency_founder (height, currency_id, account_id)");
            case 141:
                apply("CREATE INDEX IF NOT EXISTS currency_mint_height_id_idx ON currency_mint (height, currency_id, account_id)");
            case 142:
                apply("CREATE INDEX IF NOT EXISTS currency_supply_height_id_idx ON currency_supply (height, id)");
            case 143:
                apply("CREATE INDEX IF NOT EXISTS goods_height_id_idx ON goods (height, id)");
            case 144:
                apply("CREATE INDEX IF NOT EXISTS purchase_height_id_idx ON purchase (height, id)");
            case 145:
                apply("CREATE INDEX IF NOT EXISTS purchase_feedback_height_id_idx ON purchase_feedback (height, id)");
            case 146:
                apply("CREATE INDEX IF NOT EXISTS purchase_public_feedback_height_id_idx ON purchase_public_feedback (height, id)");
            case 147:
                apply("CREATE INDEX IF NOT EXISTS sell_offer_height_id_idx ON sell_offer (height, id)");
            case 148:
                apply("CREATE INDEX IF NOT EXISTS tag_height_tag_idx ON tag (height, tag)");
            case 149:
                apply("CREATE INDEX IF NOT EXISTS tagged_data_timestamp_height_id_idx ON tagged_data_timestamp (height, id)");
            case 150:
                apply("CREATE INDEX IF NOT EXISTS trade_height_db_id_idx ON trade (height DESC, db_id DESC)");
            case 151:
                apply("CREATE INDEX IF NOT EXISTS exchange_height_db_id_idx ON exchange (height DESC, db_id DESC)");
            case 152:
                apply("CREATE TABLE IF NOT EXISTS exchange_request (db_id IDENTITY, id BIGINT NOT NULL, account_id BIGINT NOT NULL, "
                        + "currency_id BIGINT NOT NULL, units BIGINT NOT NULL, rate BIGINT NOT NULL, is_buy BOOLEAN NOT NULL, "
                        + "timestamp INT NOT NULL, height INT NOT NULL)");
            case 153:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS exchange_request_id_idx ON exchange_request (id)");
            case 154:
                apply("CREATE INDEX IF NOT EXISTS exchange_request_account_currency_idx ON exchange_request (account_id, currency_id, height DESC)");
            case 155:
                apply("CREATE INDEX IF NOT EXISTS exchange_request_currency_idx ON exchange_request (currency_id, height DESC)");
            case 156:
                apply("CREATE INDEX IF NOT EXISTS exchange_request_height_db_id_idx ON exchange_request (height DESC, db_id DESC)");
            case 157:
                apply("CREATE INDEX IF NOT EXISTS exchange_request_height_idx ON exchange_request (height)");
            case 158:
                apply("CREATE TABLE IF NOT EXISTS account_ledger (db_id IDENTITY, account_id BIGINT NOT NULL, "
                        + "event_type TINYINT NOT NULL, event_id BIGINT NOT NULL, holding_type TINYINT NOT NULL, "
                        + "holding_id BIGINT, change BIGINT NOT NULL, balance BIGINT NOT NULL, "
                        + "block_id BIGINT NOT NULL, height INT NOT NULL, timestamp INT NOT NULL)");
            case 159:
                apply("CREATE TABLE IF NOT EXISTS tagged_data_extend (db_id IDENTITY, id BIGINT NOT NULL, "
                        + "extend_id BIGINT NOT NULL, height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 160:
                apply("CREATE INDEX IF NOT EXISTS tagged_data_extend_id_height_idx ON tagged_data_extend(id, height DESC)");
            case 161:
                apply("CREATE INDEX IF NOT EXISTS tagged_data_extend_height_id_idx ON tagged_data_extend(height, id)");
            case 162:
                nxt.db.FullTextTrigger.init();
                apply(null);
            case 163:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS asset_id_height_idx ON asset (id, height DESC)");
            case 164:
                apply("CREATE INDEX IF NOT EXISTS asset_height_id_idx ON asset (height, id)");
            case 165:
                apply("CREATE TABLE IF NOT EXISTS shuffling (db_id IDENTITY, id BIGINT NOT NULL, holding_id BIGINT NULL, holding_type TINYINT NOT NULL, "
                        + "issuer_id BIGINT NOT NULL, amount BIGINT NOT NULL, participant_count TINYINT NOT NULL, blocks_remaining SMALLINT NULL, "
                        + "stage TINYINT NOT NULL, assignee_account_id BIGINT NULL, registrant_count TINYINT NOT NULL, "
                        + "recipient_public_keys ARRAY, height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 166:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS shuffling_id_height_idx ON shuffling (id, height DESC)");
            case 167:
                apply("CREATE INDEX IF NOT EXISTS shuffling_holding_id_height_idx ON shuffling (holding_id, height DESC)");
            case 168:
                apply("CREATE INDEX IF NOT EXISTS shuffling_assignee_account_id_height_idx ON shuffling (assignee_account_id, height DESC)");
            case 169:
                apply("CREATE INDEX IF NOT EXISTS shuffling_height_id_idx ON shuffling (height, id)");
            case 170:
                apply("CREATE TABLE IF NOT EXISTS shuffling_participant (db_id IDENTITY, shuffling_id BIGINT NOT NULL, "
                        + "account_id BIGINT NOT NULL, next_account_id BIGINT NULL, participant_index TINYINT NOT NULL, "
                        + "state TINYINT NOT NULL, blame_data ARRAY, key_seeds ARRAY, data_transaction_full_hash BINARY(32), "
                        + "height INT NOT NULL, latest BOOLEAN NOT NULL DEFAULT TRUE)");
            case 171:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS shuffling_participant_shuffling_id_account_id_idx ON shuffling_participant "
                        + "(shuffling_id, account_id, height DESC)");
            case 172:
                apply("CREATE INDEX IF NOT EXISTS shuffling_participant_height_idx ON shuffling_participant (height, shuffling_id, account_id)");
            case 173:
                apply("CREATE TABLE IF NOT EXISTS shuffling_data (db_id IDENTITY, shuffling_id BIGINT NOT NULL, account_id BIGINT NOT NULL, "
                        + "data ARRAY, transaction_timestamp INT NOT NULL, height INT NOT NULL, "
                        + "FOREIGN KEY (height) REFERENCES PUBLIC.block (height) ON DELETE CASCADE)");
            case 174:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS shuffling_data_id_height_idx ON shuffling_data (shuffling_id, height DESC)");
            case 175:
                apply("CREATE INDEX shuffling_data_transaction_timestamp_idx ON shuffling_data (transaction_timestamp DESC)");
            case 176:
                apply("CREATE TABLE IF NOT EXISTS phasing_poll_linked_transaction (db_id IDENTITY, "
                        + "transaction_id BIGINT NOT NULL, linked_full_hash BINARY(32) NOT NULL, linked_transaction_id BIGINT NOT NULL, "
                        + "height INT NOT NULL)");
            case 177:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS phasing_poll_linked_transaction_id_link_idx "
                        + "ON phasing_poll_linked_transaction (transaction_id, linked_transaction_id)");
            case 178:
                apply("CREATE INDEX IF NOT EXISTS phasing_poll_linked_transaction_height_idx ON phasing_poll_linked_transaction (height)");
            case 179:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS phasing_poll_linked_transaction_link_id_idx "
                        + "ON phasing_poll_linked_transaction (linked_transaction_id, transaction_id)");
            case 180:
                apply("CREATE TABLE IF NOT EXISTS asset_delete (db_id IDENTITY, id BIGINT NOT NULL, asset_id BIGINT NOT NULL, "
                        + "account_id BIGINT NOT NULL, quantity BIGINT NOT NULL, timestamp INT NOT NULL, height INT NOT NULL)");
            case 181:
                apply("CREATE UNIQUE INDEX IF NOT EXISTS asset_delete_id_idx ON asset_delete (id)");
            case 182:
                apply("CREATE INDEX IF NOT EXISTS asset_delete_asset_id_idx ON asset_delete (asset_id, height DESC)");
            case 183:
                apply("CREATE INDEX IF NOT EXISTS asset_delete_account_id_idx ON asset_delete (account_id, height DESC)");
            case 184:
                apply("CREATE INDEX IF NOT EXISTS asset_delete_height_idx ON asset_delete (height)");
            case 185:
                return;
            default:
                throw new RuntimeException("Child chain " + schema + " database inconsistent with code, at update " + nextUpdate
                        + ", probably trying to run older code on newer database");
        }
    }
}