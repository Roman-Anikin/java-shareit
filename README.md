# Проект Share It

Технологии: Java 11, Spring Boot 2.7.2, PostgreSQL, H2, Maven, Docker, Hibernate, Lombok, Mockito, Logbook.

## Описание

Приложение для шеринга (от англ. share — «делиться») вещей, оно дает пользователям возможность
рассказывать, какими вещами они готовы поделиться, находить нужную вещь и брать её в аренду на какое-то
время.

### Проект состоит из двух микросервисов:

1. Gateway сервис - в нём выполняется вся валидация запросов — некорректные исключаются. После валидации в gateway
   запрос отправляется основному приложению. Запускается на порту 8080.


2. Server сервис - содержит всю основную логику и обращается к базе данных. Запускается на порту 9090. Позволяет
   пользователям:


* Добавлять и редактировать вещи. Изменить можно название, описание и статус доступ к аренде.
  Редактировать вещь может только её владелец.
* Просматривать информацию о конкретной вещи по её идентификатору. Информацию о вещи может просмотреть любой
  пользователь.
* Владелец может просматривать список всех своих вещей с указанием названия и описания для каждой.
* Поиск вещи потенциальным арендатором. Сервис ищет вещи, содержащие ключевые слова в названии или описании и
  возвращает только доступные для аренды вещи.
* Добавлять запросы на бронирование. Запрос может быть создан любым пользователем, а затем подтверждён или отклонен
  владельцем вещи.
* Получать данных о конкретном бронировании (включая его статус). Может быть выполнено либо автором бронирования, либо
  владельцем вещи, к которой относится бронирование.
* Получать список бронирований для всех вещей текущего пользователя.
* Оставлять комментарии к арендованным вещам и просматривать комментарии других пользователей.
* Добавлять новый запрос на вещь, которую пользователь хочет взять в аренду.
* Получать список своих запросов вместе с данными об ответах на них.
* Получать список запросов, созданных другими пользователями, чтобы понять, на какие из них пользователь может ответить.
* Получать данные об одном конкретном запросе вместе с данными об ответах на него. Посмотреть данные об отдельном
  запросе может любой пользователь.

Модель базы данных

![Модель базы данных](server/src/main/resources/shareit.png)

## Запуск приложения

### С помощью командной строки

Необходимые инструменты:

* [Java (JDK) 11;](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* [Apache Maven 4.x](https://maven.apache.org/users/index.html)

Находясь в корневой папке проекта, выполнить:

* mvn package
* java -jar gateway/target/shareit-gateway-0.0.1-SNAPSHOT.jar
* java -jar server/target/shareit-server-0.0.1-SNAPSHOT.jar

### С помощью Docker

Необходимые инструменты:

* [Java (JDK) 11;](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* [Apache Maven 4.x](https://maven.apache.org/users/index.html)
* [Docker](https://www.docker.com/)

Находясь в корневой папке проекта, запустить Docker и выполнить:

* docker-compose up
