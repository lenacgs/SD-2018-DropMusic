## Protocolo de comunicação 

Especificação de todos os comandos que são enviados entre o servidor RMI e os servidores Multicast. Para efeitos de simplificação, assumi que o servidor RMI é (1) e os servidores Multicast são (2).

Não podem haver "|", ";" nem "\n" nas chaves ou valores.

**;** -> serve para separação dos conjuntos chave-valor
**|** -> serve para separação da chave do valor
**\n** -> fim do comando



####User Registration

REQUEST: **type** | register ; **username** | new\_username ; **password** | new password \n

ANSWER: **type** | status ; **register** | succeeded ou failed ; admin | 0 ou 1\n

REQUEST: **type** | data\_count ; **object** | user\n

ANSWER: **type** | data\_count ; **object** | user ; **count** | <n users>\n (isto para saber se é o primeiro para ficar owner da plataforma)


####User Login

REQUEST: **type** | login ; **username** | username ; **password** | password \n

ANSWER: **type** | status ; **login** | succeeded ou failed ; \n

REQUEST: **type** | perks ; **username** | username \n

ANSWER: **type** | perks ; **user** | 1(owner) / 2(editor) / 3(normal)\n


####User Logout

REQUEST: **type** | logout ; **username** | username \n

ANSWER: **type** | status ; **logout** | succeeded ou failed \n


####Check for user perks (Owner de algum grupo, Editor de algum grupo ou normal)

REQUEST: **type** | perks ; **username** | username \n

ANSWER: **type** | perks ; **user** | 1(owner) / 2(editor) / 3(normal)\n



####Check for user perks inside a group (normal user, editor or owner?)

REQUEST: **type** | perks\_group ; **username** | username ; **groupID** | groupID \n

ANSWER: **type** | perks_\group ; **user** | "normal" or "editor" or "owner" \n


####Check for groups

REQUEST: **type** | groups ; **username** | username\n	

ANSWER: **type** | groups ; **list** | <group1,group2,...>\n
(isto para apresentar ao user todos os grupos aos quais ele pode juntar-se. É enviado o username para só devolver os grupos aos quais ele nao pertence)


####Check for own groups

REQUEST: **type** | group\_users ; **group** | group\n	

ANSWER: **type** | group\_users ; **list** | <user1,user2,...>\n
(isto para apresentar aos users que pertencem aquele grupo a notificacao de que foi alterada informacao)

####Create Group
	
REQUEST: **type** | new\_group ; **username** | username\n

ANSWER: **type** | new\_group ; **object** | groupID ; **status** | succeeded ou failed\n

####Grant Perks inside a group

REQUEST: **type** | grant\_perks\_group ; **username** | username ; **new_editor/owner** | username ; **groupID** | groupID ; **new_perks** | 1 (owner) or 2 (editor) \n

ANSWER: **type** | grant\_perks\_group ; **operation** | succeeded/failed \n

####Get group requests

REQUEST: **type** | get\_requests ; **username** | username ; **groupID** | groupID

ANSWER: **type** | get\_requests; **operation** | succeeded/failed ; (if succeeded) **list** | <user1, user2,...>

####Manage group requests

REQUEST: **type** | manage\_request ; **username** | username ; **new_user** | username ; **groupID** | groupID ; **request** | accepted/declined \n

ANSWER: **type** | manage\_request ; **operation** | succeeded/failed \n

####Expell group_user

REQUEST: **type** | expell\_user ; **username** | username ; **expelled_user** | username ; **groupID** | groupID \n

ANSWER: **type** | expell\_user ; **operation** | succeeded/failed;

####Leave group

REQUEST: **type** | leave\_group ; **username** | username ; **groupID** | groupID

ANSWER: **type** | leave\_group ; **operation** | succeeded/failed 

### REQUISITO Nº 3

Pesquisar músicas

####Search for musics, albums or artists

REQUEST: **type** | search ; **keyword** | what you search for | **object** | type of object (musics, albums, genre or artists) \n

**Se pesquisou por artistas:**

ANSWER: **type** | artist\_list ; **item\_count** | n ; **item\_0\_name** | bla bla ; **item\_1\_name** | bla bla ........ \n 

**Se pesquisou por musicas:**

ANSWER: **type** | music\_list ; **item\_count** | n ; **item\_0\_name** | bla bla ; **item\_1\_name** | bla bla ........ \n 

**Se pesquisou por álbuns:**

ANSWER: **type** | album\_list ; **item\_count** | n ; **item\_0\_name** | bla bla ; **item\_1\_name** | bla bla ........ \n 

**Se pesquisou por género:**

ANSWER: **type** | genre\_list ; **item\_count** | n ; **item\_0\_name** | bla bla ; **item\_1\_name** | bla bla ........ \n 



###REQUISITO Nº 2

Gerir artistas, álbuns e músicas

####Alterar informação de álbuns/artistas

REQUEST: **type** | change\_info ; **object** | album/artist ; **new_info** | new\_text ; **username** | username ; **group** | group_name\n

ANSWER: **type** | change\_info ; **status** | success/fail \n

O user só pode alterar informação se tiver privilégios

Se o user tiver privilégios para alterar/adicionar a informação -> success

Se o user não tiver privilégios para alterar/adicionar a informação -> fail

####Adicionar informação de álbuns/artistas

Esta função é usada quando um editor quer **acrescentar** um novo álbum, música ou artista às bases de dados, partilhando-as com grupos seleccionados

Parâmetros que são pedidos ao user para colocar no novo objeto:

Numa nova música: title, artist, genre, duration

Num novo álbum: title, artist, list of musics, year of publication, publisher, genre, description

Num novo artista: name, description, concerts, genre

Há duas funções diferentes no RMI para addInfo, visto que nas músicas e nos artistas precisamos de 4 parâmetros, mas no novo artista precisamos de 7. Por isto, há 3 tipos de requests

#####Add new music

REQUEST: **type** | add\_music ; **username** | username que vai ficar associado à adição ; **groups** | lista de grupos com quem é partilhada esta informação ; **title** | title; **artist** | artist ; **genre** | genre ; **duration** | duration \n

#####Add new artist

REQUEST: **type** | add\_artist ; **username** | username que vai ficar associado à adição ; **groups** | lista de grupos com quem é partilhada esta informação ; **name** | name; **description** | description ; **concerts** | lista de concertos próximos* ; **genre** | genre \n

\* - a lista de concertos deve conter os concertos separados por vírgulas, e cada concerto deve ser "concertVenue-city-country-year-month-day-hour"

#####Add new album

REQUEST: **type** | add\_album ; **username** | useername que vai ficar associado à adição ; **groups** | lista de grupos com quem é partilhada esta informação ; **title** | title ; **artist** | artist ; **musiclist** | lista de músicas do álbum ; **year** | ano de publicação ; **publisher** | editora ; **genre** | genre ; **description** | description \n


ANSWER: **type** | add ; **status** | success/fail ; **message** | message \n


###REQUISITO Nº 4

Consultar detalhes sobre álbum e sobre artista

###Consultar detalhes sobre um álbum

(esta mensagem é enviada, e o (2) envia uma String com tudo lá dentro

REQUEST: **type** | get\_info ; **object** | album ; **title** | album\_title \n

ANSWER: **type** | get\_info ; **info** | toda a informação numa String \n

###Consultar detalhes sobre um artista

REQUEST: **type** | get\_info ; **object** | artist ; **title** | artist\name \n

ANSWER: **type** | get\_info ; **info** | toda a informação numa String \n


###REQUISITO Nº 5

Escrever críticas a um álbum

REQUEST: **type** | review ; **album\_title** | album title ; **username** | username ; **text** | texto até 300 carateres ; **rate** | rate \n

ANSWER: **type** | review  ; **status** | success/fail \n



Dar privilégios de editor ou owner a um user

REQUEST: **type** | grant\_perks ; **perk** | (editor / user) ; **username** | username proprio ; **new\_user** | username do novo editor ; **group** | groupID \n

ANSWER: **type** | grant\_perks ; **status** | success/fail \n

(Pode ser success ou fail, dependendo se o user que estiver a dar privilégios seja ou não editor ou owner


####Neste caso, o user que recebeu os privilégios deve ser notificado imediatamente

O multicast envia para o rmi um aviso para notificar o user

**type** | notify\_user ; **username** | username ; **msg** | You've got editor perks! \n

Caso o user não esteja online, o rmi responde ao servidor que não foi possível enviar a mensagem

**type** | notify\_user ; **status** | fail ; **username** | username \n

Ainda não pensei como vai funcionar a questão das notificações caso o user não esteja online
É fácil, guardas num ficheiro / BD as mensagens e quando o user loga mandas. Precisas de um ficheiro / tabela só para isto.


#### Join group

REQUEST: **type** | join\_group ; **username** | username ; **group** | group\n

ANSWER: **type** | join\_group ; **username** | username ; **group** | group ; **status** | success/fail\n

###

####Upload de ficheiros para um servidor
REQUEST: **type** | upload ; **username** | username ; **music_title** | music title \n
