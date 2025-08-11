-- 用户表
create table if not exists "user"
(
    id           bigserial primary key,
    userAccount  varchar(256)                           not null,
    userPassword varchar(512)                           not null,
    userName     varchar(256),
    userAvatar   varchar(1024),
    userProfile  varchar(512),
    userRole     varchar(256) default 'user'            not null,
    editTime     date     default CURRENT_DATE not null,
    createTime   date     default CURRENT_DATE not null,
    updateTime   timestamp    default CURRENT_TIMESTAMP not null,
    isDelete     boolean      default false             not null,
    UNIQUE (userAccount),
    CONSTRAINT uk_userAccount UNIQUE (userAccount)
);
COMMENT ON TABLE "user" IS '用户';
COMMENT ON COLUMN "user".id IS 'id';
COMMENT ON COLUMN "user".userAccount IS '账号';
COMMENT ON COLUMN "user".userPassword IS '密码';
COMMENT ON COLUMN "user".userName IS '用户昵称';
COMMENT ON COLUMN "user".userAvatar IS '用户头像';
COMMENT ON COLUMN "user".userProfile IS '用户简介';
COMMENT ON COLUMN "user".userRole IS '用户角色：user/admin';
COMMENT ON COLUMN "user".editTime IS '编辑时间';
COMMENT ON COLUMN "user".createTime IS '创建时间';
COMMENT ON COLUMN "user".updateTime IS '更新时间';
COMMENT ON COLUMN "user".isDelete IS '是否删除';
CREATE INDEX idx_userName ON "user"(userName);
