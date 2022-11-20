package helpers

import zio.{Scope, ZIO}

import java.io.{File, FileInputStream, FileOutputStream}
import java.util.UUID

object FileHelper {

    def makeTempFileZIO(prefix: String, suffix: String): ZIO[Any, Throwable, File] =
        ZIO.from(makeTempFile(prefix, suffix))

    def makeTempFile(prefix: String, suffix: String): File = File.createTempFile(prefix, suffix)

    def makeFileZIO(path: String): ZIO[Any, Throwable, File] = for {
        newFile <- ZIO.from(new File(path))
        _ <- ZIO.attemptBlocking(newFile.getParentFile.mkdirs)
        _ <- ZIO.attemptBlocking(newFile.createNewFile())
    } yield newFile

    def makeFile(path: String): File = {
        val file = new File(path)
        file.getParentFile.mkdirs()
        file.createNewFile()
        file
    }

    def makeFileInputStream(file: File): ZIO[Any with Scope, Throwable, FileInputStream] =
        ZIO.acquireRelease(ZIO.from(new FileInputStream(file)))(fis => ZIO.succeed(fis.close()))

    def makeFileOutputStream(file: File): ZIO[Any with Scope, Throwable, FileOutputStream] =
        ZIO.acquireRelease(ZIO.from(new FileOutputStream(file)))(fis => ZIO.succeed(fis.close()))
}
