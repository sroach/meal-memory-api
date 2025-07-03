package gy.roach.health.mealmemoryapi.service

import gy.roach.health.mealmemoryapi.model.Meal
import gy.roach.health.mealmemoryapi.repository.MealRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

@Service
class MealService(private val mealRepository: MealRepository) {

    private val uploadDir = "data/uploads"

    init {
        // Create upload directory if it doesn't exist
        val uploadPath = Paths.get(uploadDir)
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath)
        }
    }

    fun saveMeal(
        image: MultipartFile,
        timestamp: String?,
        userId: String?,
        metadata: String?
    ): Meal {
        // Save the image file
        val savedFilePath = saveImageFile(image)

        // Parse timestamp if provided
        val parsedTimestamp = timestamp?.let { parseTimestamp(it) }

        // Create and save meal entity
        val meal = Meal(
            imagePath = savedFilePath,
            originalFilename = image.originalFilename,
            fileSize = image.size,
            timestamp = parsedTimestamp,
            userId = userId,
            metadata = metadata
        )

        return mealRepository.save(meal)
    }

    private fun saveImageFile(image: MultipartFile): String {
        val fileName = generateUniqueFileName(image.originalFilename)
        val filePath = Paths.get(uploadDir, fileName)

        // Create directories if they don't exist
        Files.createDirectories(filePath.parent)

        image.transferTo(filePath.toFile())

        return filePath.toString()
    }

    private fun generateUniqueFileName(originalFilename: String?): String {
        val extension = originalFilename?.substringAfterLast('.', "") ?: "jpg"
        val timestamp = System.currentTimeMillis()
        val uuid = UUID.randomUUID().toString().substring(0, 8)

        return "${timestamp}_${uuid}.${extension}"
    }

    private fun parseTimestamp(timestampStr: String): LocalDateTime? {
        return try {
            // Try parsing ISO 8601 format
            LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: DateTimeParseException) {
            try {
                // Try parsing with 'T' separator
                LocalDateTime.parse(timestampStr.replace(' ', 'T'))
            } catch (e2: DateTimeParseException) {
                null // Return null if parsing fails
            }
        }
    }

    fun findAllMeals(): List<Meal> = mealRepository.findAll()

    fun findMealsByUserId(userId: String): List<Meal> = mealRepository.findByUserId(userId)

    fun findMealById(id: Long): Meal? = mealRepository.findById(id).orElse(null)

    fun deleteMeal(id: Long): Boolean {
        return if (mealRepository.existsById(id)) {
            val meal = mealRepository.findById(id).get()
            // Delete the image file
            try {
                Files.deleteIfExists(Paths.get(meal.imagePath))
            } catch (e: Exception) {
                // Log error but continue with database deletion
            }
            mealRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}