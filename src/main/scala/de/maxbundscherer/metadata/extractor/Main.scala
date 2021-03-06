package de.maxbundscherer.metadata.extractor

import de.maxbundscherer.metadata.extractor.utils.{ ConfigurationHelper, LoggingHelper }

object Main extends App with ConfigurationHelper with LoggingHelper {

  import de.maxbundscherer.metadata.extractor.runners.{ AbstractRunner, DebugRunner }
  import de.maxbundscherer.metadata.extractor.services.{
    AwsS3Service,
    CacheService,
    LocalFileService
  }

  log.info(s"Application started (${Config.Global.message})")

  private val cacheService: CacheService         = new CacheService()
  private val awsS3Service: AwsS3Service         = new AwsS3Service(cacheService)
  private val localFileService: LocalFileService = new LocalFileService(cacheService)

  private val runner: AbstractRunner = new DebugRunner(awsS3Service, localFileService)

  runner.run

  log.info(s"Application ended")
}
