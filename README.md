
# File Filter

Утилита для фильтрации содержимого файлов по типам данных.

## Требования

- Java 25.0.1
- Gradle 9.3.0

## Сборка проекта

./gradlew build

## Запуск тестов

./gradlew test

## Запуск

./gradlew run --args="src/main/resources/in1.txt src/main/resources/in2.txt -f -p sample- -a -o ./types"