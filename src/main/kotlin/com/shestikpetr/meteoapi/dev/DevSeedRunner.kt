package com.shestikpetr.meteoapi.dev

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.core.io.Resource
import org.springframework.jdbc.datasource.init.ScriptUtils
import org.springframework.stereotype.Component
import javax.sql.DataSource

// Применяет data-dev.sql на старте приложения под профилем 'dev'
@Component
@Profile("dev")
class DevSeedRunner(
    private val dataSource: DataSource,
    @param:Value("classpath:data-dev.sql") private val seedScript: Resource,
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(DevSeedRunner::class.java)

    override fun run(args: ApplicationArguments) {
        log.info("Применяю dev seed из {}", seedScript.filename)
        executeScript()
        log.info("Dev seed применён")
    }

    private fun executeScript() {
        dataSource.connection.use { connection ->
            ScriptUtils.executeSqlScript(connection, seedScript)
        }
    }
}
