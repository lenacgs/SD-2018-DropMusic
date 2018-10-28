## Protocolo de comunicação 

Especificação de todos os comandos que são enviados entre o servidor RMI e os servidores Multicast. Para efeitos de simplificação, assumi que o servidor RMI é (1) e os servidores Multicast são (2).

Não podem haver "|", ";" nem "\n" nas chaves ou valores.

**;** -> serve para separação dos conjuntos chave-valor
**|** -> serve para separação da chave do valor
**\n** -> fim do comando



####User Registration

REQUEST: **type** | register ; **username** | new_username ; **password** | new password

ANSWER: **type** | status ; **register** | succeeded ou failed ; admin | 0 ou 1

REQUEST: **type** | data_count ; **object** | user

ANSWER: **type** | data_count ; **object** | user ; **count** | <n users> (isto para saber se é o primeiro para ficar owner da plataforma)


####User Login

REQUEST: **type** | login ; **username** | username ; **password** | password


**Se falhar**

ANSWER: **type** | login ; **operation** | failed

**Se tiver sucesso**

ANSWER: **type** | status ; **operation** | succeeded ; **perks** | perk do user* ; **notifications** | todas as notificações pendentes para esse user

\* - 1: user é owner de algum grupo

\* - 2: user é editor de algum grupo

\* - 3: user é um user normal

Para transmitir estas informações ao cliente, é retornada uma string com a seguinte estrutura:

"perk_number,notif1,notif2,..."

Cada notificação segue a seguinte estrutura:

"mensagem@timeStamp"


####User Logout

REQUEST: **type** | logout ; **username** | username

ANSWER: **type** | status ; **logout** | succeeded ou failed


####Check for groups

REQUEST: **type** | groups ; **username** | username

ANSWER: **type** | groups ; **list** | <group1,group2,...>
(isto para apresentar ao user todos os grupos aos quais ele pode juntar-se. É enviado o username para só devolver os grupos aos quais ele nao pertence)


####Create Group
	
REQUEST: **type** | new_group ; **username** | username

ANSWER: **type** | new_group ; **groupID** | groupID ; **operation** | succeeded ou failed



####Get group requests

REQUEST: **type** | get_requests ; **username** | username ; **groupID** | groupID

ANSWER: **type** | get_requests; **operation** | succeeded/failed ; (if succeeded) **list** | <user1, user2,...>


####Manage group requests

REQUEST: **type** | manage_request ; **username** | username ; **new_user** | username ; **groupID** | groupID ; **request** | accept/decline

ANSWER: **type** | manage_request ; **status** | succeeded/failed ; **operation** | accept/decline


### REQUISITO Nº 3

Pesquisar músicas

####Search for musics, albums or artists

REQUEST: **type** | search ; **username** | username ; **keyword** | what you search for | **object** | type of object (musics, albums or artists)

**Se pesquisou por artistas:**

ANSWER: **type** | artist_list ; **item_count** | n ; **item_list** | bla bla

**Se pesquisou por musicas:**

ANSWER: **type** | music_list ; **item_count** | n ; **item_list** | bla bla

**Se pesquisou por álbuns:**

ANSWER: **type** | album_list ;  **item_count** | n ; **item_list** | bla bla



###REQUISITO Nº 2

Gerir artistas, álbuns e músicas

####Alterar informação de álbuns/artistas

REQUEST: **type** | change_info ; **object** | music ; **username** | username ; **groups** | groups ; **title** | title ; **artist** | artist ; **genre** | genre ; **duration** | duration

ANSWER: **type** | change_info ; **status** | success/fail

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

REQUEST: **type** | add_music ; **username** | username que vai ficar associado à adição ; **groups** | lista de grupos com quem é partilhada esta informação ; **title** | title; **artist** | artist ; **genre** | genre ; **duration** | duration

#####Add new artist

REQUEST: **type** | add_artist ; **username** | username que vai ficar associado à adição ; **groups** | lista de grupos com quem é partilhada esta informação ; **name** | name; **description** | description ; **concerts** | lista de concertos próximos* ; **genre** | genre

\* - a lista de concertos deve conter os concertos separados por vírgulas, e cada concerto deve ser "concertVenue-city-country-year-month-day-hour"

#####Add new album

REQUEST: **type** | add_album ; **username** | useername que vai ficar associado à adição ; **groups** | lista de grupos com quem é partilhada esta informação ; **title** | title ; **artist** | artist ; **musiclist** | lista de músicas do álbum ; **year** | ano de publicação ; **publisher** | editora ; **genre** | genre ; **description** | description


ANSWER: **type** | add ; **operation** | succeeded/failed ; **error** | message


###REQUISITO Nº 4

Consultar detalhes sobre álbum e sobre artista

###Consultar detalhes sobre um álbum

(esta mensagem é enviada, e o (2) envia uma String com tudo lá dentro

REQUEST: **type** | get_info ; **object** | album ; **title** | album_title ; **artist** | artist_name

ANSWER: **type** | get_info ; **info** | toda a informação numa String

###Consultar detalhes sobre um artista

REQUEST: **type** | get_info ; **object** | artist ; **title** | artist_name

ANSWER: **type** | get_info ; **info** | toda a informação numa String

###Consultar detalhes sobre uma musica

REQUEST: **type** | get_info ; **object** | music ; **title** | music_title

ANSWER: **type** | get_info ; **info** | toda a informação numa String


###REQUISITO Nº 5

Escrever críticas a um álbum

REQUEST: **type** | review ; **album_title** | album title ; **artist_name** | artist_name ; **username** | username ; **text** | texto até 300 carateres ; **rate** | rate

ANSWER: **type** | review  ; **review** | successful/failed ; **error** | descrição do erro



###Dar privilégios de editor ou owner a um user

REQUEST: **type** | grant_perks ; **perk** | (editor / user) ; **username** | username proprio ; **new_user** | username do novo editor ; **group** | groupID

ANSWER: **type** | grant_perks ; **status** | succeeded/failed ; **error** | descrição do erro

(Pode ser success ou fail, dependendo se o user que estiver a dar privilégios seja ou não editor ou owner


#### Join group

REQUEST: **type** | join_group ; **username** | username ; **group** | group

ANSWER: **type** | join_group ; **username** | username ; **group** | group ; **status** | succeeded ; **owners** | owner_list
ou
ANSWER: **type** | join_group ; **username** | username ; **group** | group ; **status** | failed ; **error** | descrição do erro


####Upload de ficheiros para um servidor
REQUEST: **type** | upload ; **username** | username ; **music_title** | music title ; **artistName** | artistName

ANSWER: **type** | upload ; **port** | port where the server is listening
ou
ANSWER: **type** | upload ; **operation** | failed


####Download de ficheiros para um servidor
REQUEST: **type** | download ; **username** | username ; **music_title** | musicTitle ; **artistName** | artistName

ANSWER: **type** | download ; **port** | 5500

####Partilha de um ficheiro musical de forma a permitir o seu download

#####Obter a lista de músicas que o user tem no servidor associadas ao seu nome

REQUEST: **type** | get_musics ; **username** | username ;

ANSWER: **type** | get_musics; **item_count** | nº de músicas ; **music_list** | <musica1:nomeArtista,musica2:nomeArtista,musica2:nomeArtista,...>


#####Enviar a música e a lista de grupos que passarão a ter acesso ao ficheiro

REQUEST: **type** | share_music ; **username** | username ; **musicTitle** | musicTitle ; **artistName** | artistName ; **groupIDs** | <ID1,ID2,ID3,...>

O servidor multicast retorna também a lista de users que têm acesso ao ficheiro pela primeira vez, para que o RMI possa enviar uma notificação para eles.

ANSWER: **type** | share_music ; **item_count** | count ;  **user_list** | user1,user2,...



####Guardar notificação porque o user não está loggado

REQUEST: **type** | notification ; **username** | username ; **message** | mensagem da notificação

ANSWER: **type** | status ; **operation** | succeeded


####Aceder às notificações quando o usar logga

REQUEST: **type** | get_notifications ; **username** | username

ANSWER: **type** | get_notifications ; **item_count** | n ; **notifications** | String com as notificações todas