CREATE TABLE IF NOT EXISTS users (
                                     user_id SERIAL PRIMARY KEY,
                                     user_name varchar(100) NOT NULL UNIQUE,
                                     mail varchar(100) NOT NULL UNIQUE,
                                     password varchar(100),
                                     enabled boolean default false not null,
                                     user_description varchar(200) default '' not null,
                                     allow_moderator boolean default true not null,
                                     picture bytea default null
);
--ALTER TABLE users ADD COLUMN  enabled boolean default false not null ;
--ALTER TABLE users ADD COLUMN  user_description varchar(200) default '' not null ;
--ALTER TABLE users ADD COLUMN  allow_moderator boolean default true not null ;
--ALTER TABLE users ADD COLUMN picture bytea default null;

CREATE TABLE IF NOT EXISTS verification_token(
                                                 token_id SERIAL PRIMARY KEY,
                                                 user_id integer NOT NULL unique,
                                                 token varchar(200) NOT NULL,
                                                 exp_date timestamp NOT NULL,
                                                 FOREIGN KEY(user_id) REFERENCES users
);
CREATE TABLE IF NOT EXISTS frameworks (
                                          framework_id SERIAL PRIMARY KEY,
                                          framework_name varchar(50) NOT NULL,
                                          category varchar(50) NOT NULL,
                                          description varchar(500) NOT NULL,
                                          introduction varchar(5000) NOT NULL,
                                          logo varchar(150) default NULL,
                                          type varchar(100),
                                          date timestamp NOT NULL default '2020-08-03 16:56:37.125000',
                                          author int NOT NULL default 1,
                                          picture bytea default null,
                                          FOREIGN KEY(author) REFERENCES users ON DELETE CASCADE,
                                          UNIQUE (framework_name)

);
--ALTER TABLE frameworks ADD COLUMN  type varchar(100) default '' not null ;
--ALTER TABLE frameworks ADD COLUMN author int NOT NULL default 1 REFERENCES users ON DELETE set default
--ALTER TABLE frameworks ADD COLUMN date timestamp NOT NULL default '2020-08-03 16:56:37.125000'
CREATE UNIQUE INDEX IF NOT EXISTS idx_framework on frameworks(framework_name);
--ALTER TABLE IF EXISTS  votes RENAME TO framework_votes;
CREATE TABLE IF NOT EXISTS framework_votes (
                                               vote_id SERIAL PRIMARY KEY,
                                               user_id integer NOT NULL,
                                               framework_id integer NOT NULL,
                                               stars integer NOT NULL,
                                               FOREIGN KEY(framework_id) REFERENCES frameworks ON DELETE CASCADE,
                                               FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
                                               UNIQUE(user_id,framework_id)
);
--CREATE UNIQUE INDEX IF NOT EXISTS idx_framework_vote on framework_votes(user_id,framework_id);
CREATE TABLE IF NOT EXISTS comments (
                                        comment_id SERIAL PRIMARY KEY,
                                        framework_id int NOT NULL,
                                        user_id int NOT NULL,
                                        description varchar(500) NOT NULL,
                                        tstamp timestamp NOT NULL,
                                        reference int,
                                        FOREIGN KEY(framework_id) REFERENCES frameworks ON DELETE CASCADE,
                                        FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
                                        FOREIGN KEY(reference) REFERENCES comments ON DELETE CASCADE
);
--ALTER TABLE comments DROP COLUMN IF EXISTS votes_up,DROP COLUMN IF EXISTS votes_down;
CREATE TABLE IF NOT EXISTS comment_votes (
                                             vote_id SERIAL PRIMARY KEY,
                                             user_id integer NOT NULL,
                                             comment_id integer NOT NULL,
                                             vote int,
                                             FOREIGN KEY(comment_id) REFERENCES comments ON DELETE CASCADE,
                                             FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
                                             unique(user_id,comment_id)
);
--CREATE UNIQUE INDEX IF NOT EXISTS idx_comment_vote on comment_votes(user_id,comment_id);
CREATE TABLE IF NOT EXISTS content (
                                       content_id SERIAL PRIMARY KEY,
                                       framework_id int NOT NULL,
                                       user_id int NOT NULL,
                                       title varchar(100) NOT NULL,
                                       tstamp timestamp NOT NULL,
                                       link text NOT NULL,
                                       type varchar(10) NOT NULL,
                                       FOREIGN KEY(framework_id) REFERENCES frameworks ON DELETE CASCADE,
                                       FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
                                       UNIQUE(title,framework_id,type)
);
--ALTER TABLE content DROP COLUMN IF EXISTS votes_up,DROP COLUMN IF EXISTS votes_down,DROP COLUMN IF EXISTS pending;
--CREATE UNIQUE INDEX IF NOT EXISTS idx_content on content(title,type,framework_id);
CREATE TABLE IF NOT EXISTS content_votes (
                                             vote_id SERIAL PRIMARY KEY,
                                             user_id integer NOT NULL,
                                             content_id integer NOT NULL,
                                             vote int,
                                             FOREIGN KEY(content_id) REFERENCES content ON DELETE CASCADE,
                                             FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
                                             UNIQUE(user_id,content_id)
);
--ALTER TABLE content ADD COLUMN  pending boolean default false not null ;
CREATE TABLE IF NOT EXISTS verify_users (
                                            verification_id SERIAL PRIMARY KEY,
                                            framework_id int NOT NULL,
                                            user_id int NOT NULL,
                                            comment_id int,
                                            pending boolean NOT NULL DEFAULT true,
                                            FOREIGN KEY(framework_id) REFERENCES frameworks ON DELETE CASCADE,
                                            FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
                                            FOREIGN KEY(comment_id) REFERENCES comments ON DELETE SET NULL,
                                            UNIQUE(user_id,framework_id)
);
CREATE TABLE IF NOT EXISTS admins (
                                      admin_id SERIAL PRIMARY KEY,
                                      user_id int NOT NULL UNIQUE,
                                      FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS comment_report (
                              report_id SERIAL PRIMARY KEY,
                              user_id integer NOT NULL,
                              comment_id integer NOT NULL,
                              description varchar(500) NOT NULL,
                              FOREIGN KEY(comment_id) REFERENCES comments ON DELETE CASCADE,
                              FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
                              UNIQUE(user_id,comment_id)
);
CREATE TABLE IF NOT EXISTS content_report (
                              report_id SERIAL PRIMARY KEY,
                              user_id integer NOT NULL,
                              content_id integer NOT NULL,
                              description varchar(500) NOT NULL,
                              FOREIGN KEY(content_id) REFERENCES content ON DELETE CASCADE,
                              FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
                              UNIQUE(user_id,content_id)
);
CREATE TABLE IF NOT EXISTS posts
(
    post_id     SERIAL PRIMARY KEY,
    description varchar(5000)      NOT NULL,
    title       varchar(200) NOT NULL,
    tstamp timestamp NOT NULL,
    user_id     integer      NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_votes
(
    post_vote_id SERIAL PRIMARY KEY,
    user_id      integer NOT NULL,
    post_id      integer NOT NULL,
    vote         integer NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_comments
(
    post_comment_id SERIAL PRIMARY KEY,
    description     varchar(500) NOT NULL,
    tstamp          timestamp    NOT NULL,
    post_id         integer      NOT NULL,
    user_id         integer      NOT NULL,
    reference       integer,

    FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts ON DELETE CASCADE,
    FOREIGN KEY (reference) REFERENCES post_comments ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS post_comment_votes
(
    post_comment_vote_id SERIAL PRIMARY KEY,
    user_id              integer NOT NULL,
    post_comment_id      integer NOT NULL,
    vote                 int,

    FOREIGN KEY (post_comment_id) REFERENCES post_comments ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS post_tags
(
    tag_id  SERIAL PRIMARY KEY,
    tag_name VARCHAR (50) NOT NULL,
    post_id INTEGER NOT NULL,

    FOREIGN KEY (post_id) REFERENCES posts ON DELETE CASCADE,
    UNIQUE (post_id, tag_name)
);