-- Ӧ�ñ�
create table app
(
    id           bigint auto_increment comment 'id' primary key,
    appName      varchar(256)                       null comment 'Ӧ������',
    cover        varchar(512)                       null comment 'Ӧ�÷���',
    initPrompt   text                               null comment 'Ӧ�ó�ʼ���� prompt',
    codeGenType  varchar(64)                        null comment '�����������ͣ�ö�٣�',
    deployKey    varchar(64)                        null comment '�����ʶ',
    deployedTime datetime                           null comment '����ʱ��',
    priority     int      default 0                 not null comment '���ȼ�',
    userId       bigint                             not null comment '�����û�id',
    editTime     datetime default CURRENT_TIMESTAMP not null comment '�༭ʱ��',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '����ʱ��',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '����ʱ��',
    isDelete     tinyint  default 0                 not null comment '�Ƿ�ɾ��',
    UNIQUE KEY uk_deployKey (deployKey), -- ȷ�������ʶΨһ
    INDEX idx_appName (appName),         -- ��������Ӧ�����ƵĲ�ѯ����
    INDEX idx_userId (userId)            -- ���������û� ID �Ĳ�ѯ����
) comment 'Ӧ��' collate = utf8mb4_unicode_ci;


-- Ӧ�ñ�
-- Ӧ�ñ�
create table app
(
    "id"           bigserial primary key, -- id
    "appName"      varchar(256),          -- Ӧ������
    "cover"        varchar(512),          -- Ӧ�÷���
    "initPrompt"   text,                  -- Ӧ�ó�ʼ���� prompt
    "codeGenType"  varchar(64),           -- �����������ͣ�ö�٣�
    "deployKey"    varchar(64),           -- �����ʶ
    "deployedTime" timestamp,             -- ����ʱ��
    "priority"     integer default 0 not null, -- ���ȼ�
    "userId"       bigint not null,       -- �����û�id
    "editTime"     timestamp default CURRENT_TIMESTAMP not null, -- �༭ʱ��
    "createTime"   timestamp default CURRENT_TIMESTAMP not null, -- ����ʱ��
    "updateTime"   timestamp default CURRENT_TIMESTAMP not null, -- ����ʱ��
    "isDelete"     smallint default 0 not null, -- �Ƿ�ɾ��
    UNIQUE ("deployKey"), -- ȷ�������ʶΨһ
    CONSTRAINT "uk_deployKey" UNIQUE ("deployKey")
);

-- �������
create index "idx_appName" on app ("appName"); -- ��������Ӧ�����ƵĲ�ѯ����
create index "idx_userId" on app ("userId");   -- ���������û� ID �Ĳ�ѯ����

-- ��ӱ�ע��
comment on table app is 'Ӧ��';
comment on column app."id" is 'id';
comment on column app."appName" is 'Ӧ������';
comment on column app."cover" is 'Ӧ�÷���';
comment on column app."initPrompt" is 'Ӧ�ó�ʼ���� prompt';
comment on column app."codeGenType" is '�����������ͣ�ö�٣�';
comment on column app."deployKey" is '�����ʶ';
comment on column app."deployedTime" is '����ʱ��';
comment on column app."priority" is '���ȼ�';
comment on column app."userId" is '�����û�id';
comment on column app."editTime" is '�༭ʱ��';
comment on column app."createTime" is '����ʱ��';
comment on column app."updateTime" is '����ʱ��';
comment on column app."isDelete" is '�Ƿ�ɾ��';

-- 对话历史表
create table chat_history
(
    "id"          bigserial primary key, -- id
    "message"     text not null,         -- 消息
    "messageType" varchar(32) not null,  -- user/ai
    "appId"       bigint not null,       -- 应用id
    "userId"      bigint not null,       -- 创建用户id
    "createTime"  timestamp default CURRENT_TIMESTAMP not null, -- 创建时间
    "updateTime"  timestamp default CURRENT_TIMESTAMP not null, -- 更新时间
    "isDelete"    smallint default 0 not null, -- 是否删除
    CONSTRAINT "chat_history_pkey" PRIMARY KEY ("id")
);

-- 创建索引
create index "idx_appId" on chat_history ("appId");                       -- 提升基于应用的查询性能
create index "idx_createTime" on chat_history ("createTime");             -- 提升基于时间的查询性能
create index "idx_appId_createTime" on chat_history ("appId", "createTime"); -- 游标查询核心索引

-- 添加表注释和列注释
comment on table chat_history is '对话历史';
comment on column chat_history."id" is 'id';
comment on column chat_history."message" is '消息';
comment on column chat_history."messageType" is 'user/ai';
comment on column chat_history."appId" is '应用id';
comment on column chat_history."userId" is '创建用户id';
comment on column chat_history."createTime" is '创建时间';
comment on column chat_history."updateTime" is '更新时间';
comment on column chat_history."isDelete" is '是否删除';
