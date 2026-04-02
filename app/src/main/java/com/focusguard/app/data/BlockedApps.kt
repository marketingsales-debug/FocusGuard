package com.focusguard.app.data

/**
 * Lists of package names for food delivery and adult content apps to block.
 */
object BlockedApps {

    val foodDeliveryApps = setOf(
        // Global
        "com.ubercab.eats",
        "com.dd.doordash",
        "com.grubhub.android",
        "com.postmates.android",
        "com.instacart.client",
        "com.seamless.consumer",
        // India
        "in.swiggy.android",
        "com.application.zomato",
        "com.done.faasos",
        // UK/Europe
        "com.deliveroo.orderapp",
        "com.justeat.app.uk",
        "com.global.foodpanda.android",
        // Southeast Asia
        "com.grabtaxi.passenger",  // GrabFood
        "com.gojek.app",           // GoFood
        // Others
        "com.mcdonalds.app",
        "com.burgerking.bkdelivery",
        "com.subway.order",
        "com.pizzahut.phd",
        "com.dominos",
        "com.doordash.driverapp",
        "com.waitr",
        "com.slicelife",
        "com.caviar",
        "com.eatstreet",
        "com.gopuff.android",
    )

    val adultApps = setOf(
        // Browsers often used to bypass filters
        "org.torproject.torbrowser",
        "org.torproject.torbrowser_alpha",
        // Known adult content apps — redacted for safety, match by category
    )

    /**
     * Keywords used to identify food-related content on YouTube.
     */
    val foodKeywords = setOf(
        "recipe", "cooking", "food", "mukbang", "eating",
        "restaurant", "chef", "baking", "kitchen", "meal",
        "dinner", "lunch", "breakfast", "snack", "delicious",
        "tasty", "yummy", "cuisine", "asmr eating", "food review",
        "what i eat", "grocery", "food haul", "pizza", "burger",
        "sushi", "ramen", "noodle", "cake", "dessert",
        "bbq", "grill", "fry", "cook with me", "food challenge",
        "eating show", "street food", "food tour", "biryani",
        "paneer", "chicken", "mutton", "fish fry",
    )

    /**
     * Domains to block for adult content via DNS filter.
     */
    val blockedDomains = setOf(
        "pornhub.com",
        "xvideos.com",
        "xnxx.com",
        "xhamster.com",
        "redtube.com",
        "youporn.com",
        "tube8.com",
        "spankbang.com",
        "eporner.com",
        "tnaflix.com",
        "porntrex.com",
        "hqporner.com",
        "pornpics.com",
        "daftsex.com",
        "porn.com",
        "4tube.com",
        "drtuber.com",
        "txxx.com",
        "nuvid.com",
        "fapvid.com",
    )

    /**
     * Food delivery website domains to also block in browser.
     */
    val foodDomains = setOf(
        "ubereats.com",
        "doordash.com",
        "grubhub.com",
        "postmates.com",
        "seamless.com",
        "swiggy.com",
        "zomato.com",
        "deliveroo.com",
        "just-eat.com",
        "foodpanda.com",
        "instacart.com",
        "gopuff.com",
    )
}
