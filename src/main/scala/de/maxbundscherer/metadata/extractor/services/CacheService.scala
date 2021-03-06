package de.maxbundscherer.metadata.extractor.services

import de.maxbundscherer.metadata.extractor.utils.JsonHelper

class CacheService() extends AbstractService with JsonHelper {

  import de.maxbundscherer.metadata.extractor.aggregates.AwsS3Aggregate
  import de.maxbundscherer.metadata.extractor.aggregates.LocalAggregate

  import com.amazonaws.services.s3.model.S3ObjectSummary
  import scala.util.Try
  import better.files._
  import java.io.{ File => JFile }
  import scala.util.{ Failure, Success }

  private val cachedAwsFileInfosFilename: String   = "aws_fileInfos.json"
  private val cachedLocalFileInfosFilename: String = "local_fileInfos.json"

  /**
    * Get file info from cache
    * @return FileInfos
    */
  def getCachedLocalFileInfos: Try[Vector[LocalAggregate.FileInfo]] =
    Try {
      val jsonData: String =
        File(s"${Config.Global.cacheDirectory}$cachedLocalFileInfosFilename").contentAsString
      Json.Local.convertFileInfosFromJson(jsonData) match {
        case Failure(exception) => throw exception
        case Success(fileKeys)  => fileKeys
      }
    }

  /**
    * Write file info to cache
    * @param data FileInfos
    * @return Try with filePath
    */
  def writeCachedLocalFileInfos(data: Vector[LocalAggregate.FileInfo]): Try[String] =
    Try {
      Json.Local.convertFileInfosToJson(data) match {
        case Failure(exception) => throw exception
        case Success(jsonContent) =>
          File(s"${Config.Global.cacheDirectory}")
            .createDirectoryIfNotExists()
          File(s"${Config.Global.cacheDirectory}$cachedLocalFileInfosFilename")
            .createFileIfNotExists()
            .write(jsonContent)
            .pathAsString
      }
    }

  /**
    * Get file info from cache
    * @return FileInfos
    */
  def getCachedAwsFileInfos: Try[Vector[AwsS3Aggregate.FileInfo]] =
    Try {
      val jsonData: String =
        File(s"${Config.Global.cacheDirectory}$cachedAwsFileInfosFilename").contentAsString
      Json.AwsS3.convertFileInfosFromJson(jsonData) match {
        case Failure(exception) => throw exception
        case Success(fileKeys)  => fileKeys
      }
    }

  /**
    * Write file info to cache
    * @param data FileInfos
    * @return Try with filePath
    */
  def writeCachedAwsFileInfos(data: Vector[AwsS3Aggregate.FileInfo]): Try[String] =
    Try {
      Json.AwsS3.convertFileInfosToJson(data) match {
        case Failure(exception) => throw exception
        case Success(jsonContent) =>
          File(s"${Config.Global.cacheDirectory}")
            .createDirectoryIfNotExists()
          File(s"${Config.Global.cacheDirectory}$cachedAwsFileInfosFilename")
            .createFileIfNotExists()
            .write(jsonContent)
            .pathAsString
      }
    }

}
