
CREATE TABLE public.categorie (
	"name" varchar NULL,
	id bigint NOT NULL,
	CONSTRAINT categorie_pk PRIMARY KEY (id)
);


CREATE TABLE public.articles (
	id int8 NOT NULL,
	"name" varchar NULL,
	CONSTRAINT articles_pk PRIMARY KEY (id)
);

