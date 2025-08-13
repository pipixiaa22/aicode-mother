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

