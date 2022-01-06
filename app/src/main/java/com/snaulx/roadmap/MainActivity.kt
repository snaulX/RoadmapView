package com.snaulx.roadmap

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rectStyle = RectStyle(Color.RED, 50F, 300F, 100F, 10F, 20F)
        val roadmap = tree<String> {
            node("I am main node", "Also value") {
                branch("Im branch") {
                    branch("Child 1")
                    branch("Child", "2")
                }
            }
        }.paintTree(30F, Color.BLACK, NodeStyle(10F,
            rectStyle, 20F), BranchStyle(10F, rectStyle, 30F))
        //setContentView(R.layout.activity_main)
        setContentView(RoadmapView(this, roadmap))
    }
}