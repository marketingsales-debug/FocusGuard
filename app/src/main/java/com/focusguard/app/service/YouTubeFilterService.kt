package com.focusguard.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.focusguard.app.data.BlockedApps
import com.focusguard.app.data.PrefsManager
import com.focusguard.app.util.ScheduleUtil

/**
 * Accessibility service that monitors YouTube and hides food-related content
 * by scrolling past or collapsing food video cards.
 */
class YouTubeFilterService : AccessibilityService() {

    private lateinit var prefs: PrefsManager
    private var lastProcessedTime = 0L
    private val debounceMs = 300L

    override fun onCreate() {
        super.onCreate()
        prefs = PrefsManager(this)
    }

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            notificationTimeout = 100
            packageNames = arrayOf("com.google.android.youtube")
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (!prefs.isFilterYouTube) return
        if (!ScheduleUtil.isBlockingActiveNow(prefs)) return

        // Debounce to avoid excessive processing
        val now = System.currentTimeMillis()
        if (now - lastProcessedTime < debounceMs) return
        lastProcessedTime = now

        val rootNode = rootInActiveWindow ?: return
        try {
            scanAndHideFoodContent(rootNode)
        } finally {
            rootNode.recycle()
        }
    }

    private fun scanAndHideFoodContent(node: AccessibilityNodeInfo) {
        // Look for text nodes that contain food keywords
        if (node.text != null) {
            val text = node.text.toString().lowercase()
            val containsFood = BlockedApps.foodKeywords.any { keyword ->
                text.contains(keyword)
            }

            if (containsFood) {
                // Try to find the parent video card and dismiss/hide it
                hideVideoCard(node)
                return
            }
        }

        // Recursively check children
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            try {
                scanAndHideFoodContent(child)
            } finally {
                child.recycle()
            }
        }
    }

    private fun hideVideoCard(node: AccessibilityNodeInfo) {
        // Walk up to find the video container/card
        var current: AccessibilityNodeInfo? = node
        var attempts = 0

        while (current != null && attempts < 10) {
            // Try to find a "Not interested" or dismiss option
            val menuButton = findMenuButton(current)
            if (menuButton != null) {
                menuButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                // After clicking menu, look for "Not interested"
                android.os.Handler(mainLooper).postDelayed({
                    clickNotInterested()
                }, 200)
                return
            }

            // If we can't find a menu, try scrolling past this item
            val parent = current.parent
            current = parent
            attempts++
        }

        // Fallback: perform a scroll to skip past the food content
        val rootNode = rootInActiveWindow ?: return
        try {
            rootNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
        } finally {
            rootNode.recycle()
        }
    }

    private fun findMenuButton(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        // Look for the three-dot menu button on YouTube video cards
        if (node.viewIdResourceName?.contains("menu") == true ||
            node.contentDescription?.toString()?.lowercase()?.contains("more") == true
        ) {
            return node
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findMenuButton(child)
            if (found != null) return found
            child.recycle()
        }
        return null
    }

    private fun clickNotInterested() {
        val rootNode = rootInActiveWindow ?: return
        try {
            val nodes = rootNode.findAccessibilityNodeInfosByText("Not interested")
            if (nodes.isNotEmpty()) {
                nodes[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                nodes.forEach { it.recycle() }
            }
        } finally {
            rootNode.recycle()
        }
    }

    override fun onInterrupt() {
        // Service interrupted
    }
}
