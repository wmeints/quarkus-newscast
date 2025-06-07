CREATE TABLE podcast_host (
    id bigint generated always as identity,
    name varchar(250) not null,
    style_instructions text not null,
    language_patterns text not null,
    voice_id varchar(250) not null,
    index int not null,
    PRIMARY KEY (id) 
);

INSERT INTO podcast_host(index, name, style_instructions, language_patterns, voice_id) values (
    1,
    'Joop Snijder',
    '',
    '',
    'Vm0vfS4svZOhBRMa4ap3'
);

INSERT INTO podcast_host(index, name, style_instructions, language_patterns, voice_id) values (
    2,
    'Willem Meints',
    '',
    '',
    'XeK7XhqnGf6zErDLDutX'
);