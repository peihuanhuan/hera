create
database hero;



INSERT INTO `config` (`appid`, `key`, `desc`, `value`, `expire_at`, `deleted`, `create_time`, `update_time`)
VALUES ('', 'subscribe_reply_content', '关注后的回复内容',
        '欢迎关注我\n💨这是个充话费的公众号，还可以超低价冲各种会员\n\n\n特价话费通道：\n<a href=\"https://cdn.wxthe.com/life/#/pages/act/phone?appid=zy149890c02e76f180&channel=fuwuhao_subscribe\">92折优惠充话费</a>\n\n\n60种会员低价直冲：\n<a href=\"https://cdn.wxthe.com/life/#/?appid=zy149890c02e76f180&channel=fuwuhao_subscribe\">全网低价会员四折起</a>',
        NULL, 0, now(), now());

INSERT INTO `config` (`appid`, `key`, `desc`, `value`, `expire_at`, `deleted`, `create_time`, `update_time`)
VALUES ('', 'first_subscribe_present_points', '首次关注赠送的积分', '100', NULL, 0, now(), now());

INSERT INTO `config` (`appid`, `key`, `desc`, `value`, `expire_at`, `deleted`, `create_time`, `update_time`)
VALUES ('', 'sign_present_points_expect', '每次签到赠送积分的期望值', '30', NULL, 0, now(), now());

INSERT INTO `config` (`appid`, `key`, `desc`, `value`, `expire_at`, `deleted`, `create_time`, `update_time`)
VALUES ('', 'sign_present_points_variance', '每次签到赠送积分的方差', '12', NULL, 0, now(), now());

INSERT INTO `config` (`appid`, `key`, `desc`, `value`, `expire_at`, `deleted`, `create_time`, `update_time`)
VALUES ('', 'huafei', '话费推荐语', '小宝贝~

慢充为 94 折特价话费，72小时内到账，不接急单哦~

快充2小时内到账，优惠0.5元

<a>➜ 戳我进入充值</a>

如有问题，请联系客服~', NULL, 0, now(), now());





