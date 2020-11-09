package de.maxbundscherer.metadata.extractor.services

import de.maxbundscherer.metadata.extractor.utils.Configuration
import org.slf4j.Logger

import scala.util.{ Failure, Success }

class FileService()(implicit log: Logger) extends Configuration {

  import de.maxbundscherer.metadata.extractor.utils.JSON
  import de.maxbundscherer.metadata.extractor.aws.aggregates.AwsAggregate

  import com.amazonaws.services.s3.model.S3ObjectSummary
  import scala.util.Try
  import better.files._
  import java.io.{ File => JFile }

  log.debug("FileService started")

  private val fileKeysFilename: String = "fileKeysCache.json"
  private val json: JSON               = new JSON()

  def getCachedAwsFileInfos: Try[Vector[AwsAggregate.FileInfo]] =
    Try {
      val jsonData: String =
        File(s"${Config.Global.cacheDirectory}$fileKeysFilename").contentAsString
      this.json.convertAwsFileInfosFromJSON(jsonData) match {
        case Failure(exception) => throw exception
        case Success(fileKeys)  => fileKeys
      }
    }

  /**
    * Write file info to cache
    * @param data fileKeys
    * @return Try with filePath
    */
  def writeCachedAwsFileInfos(data: Vector[AwsAggregate.FileInfo]): Try[String] =
    Try {
      this.json.convertAwsFileInfosToJSON(data) match {
        case Failure(exception) => throw exception
        case Success(jsonContent) =>
          File(s"${Config.Global.cacheDirectory}")
            .createDirectoryIfNotExists()
          File(s"${Config.Global.cacheDirectory}$fileKeysFilename")
            .createFileIfNotExists()
            .write(jsonContent)
            .pathAsString
      }
    }

}
