package com.example.myapplication

//use this function help show images in app from drawable resource
// Define an object named ImageFunction to encapsulate related functions
object ImageFunction {
    fun loadImage(imageId: String): Int {
        // Look up the imageId in the drawableMap and return the corresponding drawable resource ID
        // If the imageId is not found, return the default image resource ID
        return drawableMap[imageId] ?: R.drawable.default_image
    }

// Define a mapping between bookId and drawable resource ID
private val drawableMap: Map<String, Int> = mapOf(
        "1" to R.drawable.carrie,
        "2" to R.drawable.abroadinjapan,
        "3" to R.drawable.bedtimestories,
        "4" to R.drawable.dogman,
        "5" to R.drawable.mebeforeyou
// Add more mappings as needed
    )
}