package com.snaulx.roadmap

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.ColorInt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Android Roadmap"
        val roadmap = tree<String> {
            node("Pick a language") {
                branch("Kotlin")
                branch("Java")
            }
            node("Fundamentals") {
                branch("Install Android Studio") {
                    branch("Visit site")
                    branch("Download")
                }
                branch("Learn Language") {
                    branch("Learn Basics", "Learn OOP")
                }
                branch("Left")
                branch("Right")
            }
            node("Version Control Systems", "Repo Hosting Services")
        }.paintTree(padding = 30F,
            lineColor = Color.rgb(129, 43, 178), lineWidth = 4F,
            textColor = Color.BLACK,
            NodeStyle(6.7F, makeRectStyle(Color.rgb(255, 228, 151), 400F), 40F),
            BranchStyle(7.5F, makeRectStyle(Color.rgb(255, 255, 7)), 30F),
            BranchStyle(5F, makeRectStyle(Color.rgb(204, 204, 204)), 20F),
            BranchStyle(5F, makeRectStyle(Color.rgb(255, 194, 36)), 25F),
        )
        //setContentView(R.layout.activity_main)
        setContentView(RoadmapView(this, roadmap))
    }

    private fun makeRectStyle(@ColorInt color: Int, width: Float = 300F) = RectStyle(color, 31F, width, 100F, 10F, 20F)
}