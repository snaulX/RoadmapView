package com.snaulx.roadmap

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rectStyle = RectStyle(Color.CYAN, 30F, 300F, 100F, 10F, 20F)
        val roadmap = tree<String> {
            node("Pick a language") {
                branch("Kotlin")
                branch("Java")
            }
            node("Fundamentals") {
                branch("Install Android Studio") {
                    branch("Visit site") {
                        branch("A")
                    }
                    branch("B") {
                        branch("C")
                    }
                }
                branch("Learn Language") {
                    branch("Learn Basics of Language", "Learn OOP")
                }
                branch("Left")
                branch("Right")
            }
            node("Version Control Systems", "Repo Hosting Services")
        }.paintTree(30F, Color.BLACK,
            NodeStyle(10F, rectStyle, 20F),
            BranchStyle(10F, rectStyle, 30F),
            BranchStyle(5F, rectStyle, 10F),
            BranchStyle(2F, rectStyle, 15F),
        )
        //setContentView(R.layout.activity_main)
        setContentView(RoadmapView(this, roadmap))
    }
}