-- 应用表
create table app
(
    id           bigint auto_increment comment 'id' primary key,
    appName      varchar(256)                       null comment '应用名称',
    cover        varchar(512)                       null comment '应用封面',
    initPrompt   text                               null comment '应用初始化的 prompt',
    codeGenType  varchar(64)                        null comment '代码生成类型（枚举）',
    deployKey    varchar(64)                        null comment '部署标识',
    deployedTime datetime                           null comment '部署时间',
    priority     int      default 0                 not null comment '优先级',
    userId       bigint                             not null comment '创建用户id',
    editTime     datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    UNIQUE KEY uk_deployKey (deployKey), -- 确保部署标识唯一
    INDEX idx_appName (appName),         -- 提升基于应用名称的查询性能
    INDEX idx_userId (userId)            -- 提升基于用户 ID 的查询性能
) comment '应用' collate = utf8mb4_unicode_ci;


-- 应用表
-- 应用表
create table app
(
    "id"           bigserial primary key, -- id
    "appName"      varchar(256),          -- 应用名称
    "cover"        varchar(512),          -- 应用封面
    "initPrompt"   text,                  -- 应用初始化的 prompt
    "codeGenType"  varchar(64),           -- 代码生成类型（枚举）
    "deployKey"    varchar(64),           -- 部署标识
    "deployedTime" timestamp,             -- 部署时间
    "priority"     integer default 0 not null, -- 优先级
    "userId"       bigint not null,       -- 创建用户id
    "editTime"     timestamp default CURRENT_TIMESTAMP not null, -- 编辑时间
    "createTime"   timestamp default CURRENT_TIMESTAMP not null, -- 创建时间
    "updateTime"   timestamp default CURRENT_TIMESTAMP not null, -- 更新时间
    "isDelete"     smallint default 0 not null, -- 是否删除
    UNIQUE ("deployKey"), -- 确保部署标识唯一
    CONSTRAINT "uk_deployKey" UNIQUE ("deployKey")
);

-- 添加索引
create index "idx_appName" on app ("appName"); -- 提升基于应用名称的查询性能
create index "idx_userId" on app ("userId");   -- 提升基于用户 ID 的查询性能

-- 添加表注释
comment on table app is '应用';
comment on column app."id" is 'id';
comment on column app."appName" is '应用名称';
comment on column app."cover" is '应用封面';
comment on column app."initPrompt" is '应用初始化的 prompt';
comment on column app."codeGenType" is '代码生成类型（枚举）';
comment on column app."deployKey" is '部署标识';
comment on column app."deployedTime" is '部署时间';
comment on column app."priority" is '优先级';
comment on column app."userId" is '创建用户id';
comment on column app."editTime" is '编辑时间';
comment on column app."createTime" is '创建时间';
comment on column app."updateTime" is '更新时间';
comment on column app."isDelete" is '是否删除';

