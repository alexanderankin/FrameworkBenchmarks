cmake_minimum_required(VERSION 3.11.0 FATAL_ERROR)

project(cutelyst_benchmarks LANGUAGES CXX)

cmake_policy(SET CMP0069 NEW)

include(FetchContent)

FetchContent_Declare(
  mimalloc
  GIT_REPOSITORY https://github.com/microsoft/mimalloc.git
  GIT_TAG        b3b0fb58326a96beea1f4872bc1489d1d9fda7e0 # v2.0.2
)
FetchContent_MakeAvailable(mimalloc)

find_package(Qt6 6.2.0 REQUIRED COMPONENTS Core Network Sql)
find_package(ASqlQt6 0.74 REQUIRED)
find_package(Cutelyst3Qt6 3.6 REQUIRED)
find_package(Cutelee6Qt6 6.1.0 REQUIRED)
find_package(PostgreSQL REQUIRED)

# Auto generate moc files
set(CMAKE_AUTOMOC ON)

# As moc files are generated in the binary dir, tell CMake
# to always look for includes there:
set(CMAKE_INCLUDE_CURRENT_DIR ON)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

file(GLOB_RECURSE TEMPLATES_SRC root/*)

set(cutelyst_benchmarks_SRCS
    cutelyst-benchmarks.cpp
    cutelyst-benchmarks.h
    databaseupdatestest.cpp
    databaseupdatestest.h
    fortunetest.cpp
    fortunetest.h
    jsontest.cpp
    jsontest.h
    multipledatabasequeriestest.cpp
    multipledatabasequeriestest.h
    plaintexttest.cpp
    plaintexttest.h
    singledatabasequerytest.cpp
    singledatabasequerytest.h
    cachedqueries.h
    cachedqueries.cpp
    ${TEMPLATES_SRC}
)

# Create the application
add_library(cutelyst_benchmarks SHARED ${cutelyst_benchmarks_SRCS})

# Link to Cutelyst
target_link_libraries(cutelyst_benchmarks
    PUBLIC
    Cutelyst::Core
    Cutelyst::Utils::Sql
    Cutelyst::View::Cutelee
    Qt::Core
    Qt::Network
    Qt::Sql
    ASql::Core
    ASql::Pg
)

add_executable(cutelyst-benchmarks ${cutelyst_benchmarks_SRCS} main.cpp)
target_link_libraries(cutelyst-benchmarks
    PUBLIC
    Cutelyst::Core
    Cutelyst::Server
    Cutelyst::Utils::Sql
    Cutelyst::View::Cutelee
    Qt::Core
    Qt::Network
    Qt::Sql
    ASql::Core
    ASql::Pg
    mimalloc
)
if (mimalloc_FOUND)
    target_link_libraries(cutelyst-benchmarks PUBLIC mimalloc)
endif ()
