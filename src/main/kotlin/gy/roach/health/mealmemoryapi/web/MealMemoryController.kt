package gy.roach.health.mealmemoryapi.web

import gy.roach.health.mealmemoryapi.service.MealService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping("/api/v1/meal")
class MealMemoryController(private val mealService: MealService) {

    @PostMapping("/upload")
    fun upload(
        @RequestParam("image") image: MultipartFile,
        @RequestParam("timestamp", required = false) timestamp: String?,
        @RequestParam("userId", required = false) userId: String?,
        @RequestParam("feeling", required = false) feeling: String?,
        @RequestParam("metadata", required = false) metadata: String?
    ): ResponseEntity<Map<String, Any>> {

        // Validate that the image file is present
        if (image.isEmpty) {
            return ResponseEntity.badRequest().body(
                mapOf<String, Any>("error" to "Image file is required")
            )
        }

        // Combine feeling with metadata if provided
        val updatedMetadata = if (feeling != null) {
            val metadataMap = metadata?.let { 
                try { 
                    mapOf<String, Any>("originalMetadata" to it) 
                } catch (e: Exception) { 
                    emptyMap<String, Any>() 
                }
            } ?: emptyMap()

            val combinedMap = metadataMap + mapOf("feeling" to feeling)
            combinedMap.toString()
        } else {
            metadata
        }

        return try {
            val savedMeal = mealService.saveMeal(image, timestamp, userId, updatedMetadata)

            val response = mapOf<String, Any>(
                "message" to "Meal uploaded successfully",
                "id" to savedMeal.id,
                "filename" to (savedMeal.originalFilename ?: ""),
                "size" to (savedMeal.fileSize ?: 0L),
                "timestamp" to (savedMeal.timestamp?.toString() ?: ""),
                "userId" to (savedMeal.userId ?: ""),
                "createdAt" to savedMeal.createdAt.toString()
            )

            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(
                mapOf<String, Any>("error" to "Failed to save meal: ${e.message}")
            )
        }
    }

    @GetMapping("/all")
    @ResponseBody
    fun getAllMeals(): ResponseEntity<List<Map<String, Any?>>> {
        val meals = mealService.findAllMeals()
        val response = meals.map { meal ->
            mapOf<String, Any?>(
                "id" to meal.id,
                "originalFilename" to meal.originalFilename,
                "fileSize" to meal.fileSize,
                "timestamp" to meal.timestamp?.toString(),
                "userId" to meal.userId,
                "metadata" to meal.metadata,
                "createdAt" to meal.createdAt.toString()
            )
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/user/{userId}")
    @ResponseBody
    fun getMealsByUserId(@PathVariable userId: String): ResponseEntity<List<Map<String, Any?>>> {
        val meals = mealService.findMealsByUserId(userId)
        val response = meals.map { meal ->
            mapOf<String, Any?>(
                "id" to meal.id,
                "originalFilename" to meal.originalFilename,
                "fileSize" to meal.fileSize,
                "timestamp" to meal.timestamp?.toString(),
                "userId" to meal.userId,
                "metadata" to meal.metadata,
                "createdAt" to meal.createdAt.toString()
            )
        }
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    fun deleteMeal(@PathVariable id: Long): ResponseEntity<Map<String, Any>> {
        return if (mealService.deleteMeal(id)) {
            ResponseEntity.ok(mapOf<String, Any>("message" to "Meal deleted successfully"))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
