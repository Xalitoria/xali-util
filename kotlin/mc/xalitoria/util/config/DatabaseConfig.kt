package mc.xalitoria.util.config

import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** The kinds of database a plugin can store its data in. */
enum class DatabaseType {
    SQLITE,
    MARIADB,
    MYSQL;

    /** MariaDB and MySQL are both reached over the network, with a host, a login and a pool. */
    val isRemote: Boolean get() = this != SQLITE

    companion object {
        /** The type named by [name] in any case. Throws [IllegalArgumentException] naming the choices when there is none. */
        fun of(name: String): DatabaseType = entries.firstOrNull { it.name.equals(name.trim(), ignoreCase = true) }
            ?: throw IllegalArgumentException("Unknown database type '$name': use one of ${entries.joinToString { it.name.lowercase() }}")
    }
}

/**
 * What the database section of a plugin's configuration holds. A plugin that needs more than [DatabaseConfig] has
 * (a server name, say) writes its own class and implements this, so the helpers below work on it too.
 */
interface DatabaseSettings {
    /** `sqlite`, `mariadb` or `mysql`, in any case; see [databaseType]. */
    val type: String

    /** Prepended to every table name. */
    val tablePrefix: String
    val sqlite: DatabaseConfig.Sqlite
    val mysql: DatabaseConfig.Mysql
}

/** [DatabaseSettings.type] as a [DatabaseType]. */
val DatabaseSettings.databaseType: DatabaseType get() = DatabaseType.of(type)

/**
 * The database section a plugin keeps in its configuration: a local SQLite file or a MariaDB/MySQL server. It holds
 * only the settings. Connecting is the plugin's, as this library has no database driver of its own.
 *
 * Defaults differ per plugin (the prefix, the file and the database are named after it), so each one writes them out:
 *
 * ```kotlin
 * val database: DatabaseConfig = DatabaseConfig(
 *     tablePrefix = "xalirefer_",
 *     sqlite = DatabaseConfig.Sqlite("xalirefer.db"),
 *     mysql = DatabaseConfig.Mysql(database = "refer"),
 * )
 * ```
 */
@Serializable
data class DatabaseConfig(
    @YamlComment("Database types: sqlite, mariadb or mysql. Use mariadb for MariaDB: it has its own driver.")
    override val type: String = "sqlite",
    @YamlComment("Prepended to every table name: letters, digits and underscores. Needs a restart; a new prefix means new, empty tables.")
    @SerialName("table-prefix")
    override val tablePrefix: String = "xali_",
    override val sqlite: Sqlite = Sqlite(),
    @YamlComment("MariaDB/MySQL connection settings")
    override val mysql: Mysql = Mysql(),
) : DatabaseSettings {

    /** A SQLite database: a file in the plugin's folder. */
    @Serializable
    data class Sqlite(
        val filename: String = "xali.db",
    )

    /** A MariaDB or MySQL database. */
    @Serializable
    data class Mysql(
        val host: String = "localhost",
        val port: Int = 3306,
        val database: String = "xali",
        val username: String = "root",
        val password: String = "",
        @SerialName("use-ssl")
        val useSsl: Boolean = false,
        @SerialName("pool-size")
        val poolSize: Int = 10,
    ) {
        /**
         * The JDBC URL for [type], which must be remote. MariaDB gets its own driver's URL: Connector/J asks a MariaDB
         * server for INFORMATION_SCHEMA columns it doesn't have and fails before the schema is read.
         */
        fun jdbcUrl(type: DatabaseType): String = when (type) {
            DatabaseType.MARIADB -> "jdbc:mariadb://$host:$port/$database?sslMode=${if (useSsl) "trust" else "disable"}"
            DatabaseType.MYSQL -> "jdbc:mysql://$host:$port/$database?useSSL=$useSsl"
            DatabaseType.SQLITE -> throw IllegalArgumentException("SQLite has no host: it is a file")
        }

        /** The JDBC driver class for [type], which must be remote. */
        fun driverClass(type: DatabaseType): String = when (type) {
            DatabaseType.MARIADB -> "org.mariadb.jdbc.Driver"
            DatabaseType.MYSQL -> "com.mysql.cj.jdbc.Driver"
            DatabaseType.SQLITE -> throw IllegalArgumentException("SQLite has no host: it is a file")
        }
    }

    companion object {
        /** The only characters a table prefix may have, as it ends up in SQL. */
        val PREFIX = Regex("[A-Za-z0-9_]*")

        /** [prefix] if it is a valid [DatabaseSettings.tablePrefix], otherwise an [IllegalArgumentException] saying what is wrong. */
        fun checkPrefix(prefix: String): String {
            require(PREFIX.matches(prefix)) { "database.table-prefix may only contain letters, digits and underscores" }
            return prefix
        }
    }
}
