-- IM 社交支付测试数据清理（每个用例结束后执行，仅清数据不删表）
DELETE FROM "im_red_packet";
DELETE FROM "im_bank_card";
DELETE FROM "im_transaction_log";
DELETE FROM "im_transfer";
DELETE FROM "im_red_packet_grab";
DELETE FROM "im_pay_password";
DELETE FROM "im_user_wallet";
