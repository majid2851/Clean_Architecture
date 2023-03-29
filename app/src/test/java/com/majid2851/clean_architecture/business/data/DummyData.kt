package com.majid2851.clean_architecture.business.data

import com.example.clean_architecture.business.domain.model.DummyData
import com.google.gson.JsonArray

fun getDummyData(): ArrayList<DummyData> {
    val list:ArrayList<DummyData> = ArrayList()
    list.add(DummyData(
        "2474abea-7584-486b-9f88-87a21870b0ec",
        "Vancouver PNE 2019",
        "Here is Jess and I at the Vancouver PNE. We ate a lot of food.",
        "2019-04-14 08:41:22 AM",
        "2019-04-14 07:05:11 AM"))

    return list
//    val array:JsonArray=[
//        {
//            "id": "2474abea-7584-486b-9f88-87a21870b0ec",
//            "title": "Vancouver PNE 2019",
//            "body": "Here is Jess and I at the Vancouver PNE. We ate a lot of food.",
//            "updated_at": "2019-04-14 08:41:22 AM",
//            "created_at": "2019-04-14 07:05:11 AM"
//        },
//        {
//            "id": "2474fbaa-7884-4h6b-9b8z-87a21670b0ec",
//            "title": "Ready for a Walk",
//            "body": "Here I am at the park with my dogs Kiba and Maizy. Maizy is the smaller one and Kiba is the larger one.",
//            "updated_at": "2019-04-17 11:05:24 PM",
//            "created_at": "2019-04-15 04:44:57 AM"
//        },
//        {
//            "id": "2474mbaa-7884-4htb-9baz-87a216a0b0ec",
//            "title": "Maizy Sleeping",
//            "body": "I took this picture while Maizy was sleeping on the couch. She's very cute.",
//            "updated_at": "2019-02-01 01:55:53 AM",
//            "created_at": "2019-01-24 12:19:35 PM"
//        },
//        {
//            "id": "2474fpaa-k884-4u6b-9biz-87am1670b0ec",
//            "title": "My Brother Blake",
//            "body": "This is a picture of my brother Blake and I. We were taking some pictures for his website.",
//            "updated_at": "2019-12-14 03:05:16 PM",
//            "created_at": "2019-12-13 07:05:17 AM"
//        },
//        {
//            "id": "2474abaa-788a-4a6b-948z-87a2167hb0ec",
//            "title": "Lounging Dogs",
//            "body": "Kiba and Maizy are laying in the sun relaxing.",
//            "updated_at": "2019-11-14 06:12:44 AM",
//            "created_at": "2019-10-14 02:47:13 PM"
//        },
//        {
//            "id": "24742baa-78j4-4z6b-9b8l-87a11670b0ec",
//            "title": "Mountains in Washington",
//            "body": "This is an image I found somewhere on the internert. I love pictures like this. I believe it's in Washington, U.S.A.",
//            "updated_at": "2019-05-19 11:34:16 PM",
//            "created_at": "2019-04-25 05:16:36 AM"
//        },
//        {
//            "id": "2g74fbaa-78h4-4hab-9b85-87l21670b0ec",
//            "title": "France Mountain Range",
//            "body": "Another beautiful picture of nature. You can find more pictures like this one on Reddit.com, in the subreddit: '/r/earthporn'.",
//            "updated_at": "2019-10-01 12:22:46 AM",
//            "created_at": "2019-09-19 09:36:57 PM"
//        },
//        {
//            "id": "2477fbaa-7b84-4hjb-9bkl-87a2a670b0ec",
//            "title": "Aldergrove Park",
//            "body": "I walk Kiba and Maizy pretty much every day. Usually we go to a park in Aldergrove. It takes about 1 hour, 15 minutes to walk around the entire park.",
//            "updated_at": "2019-06-12 12:58:55 AM",
//            "created_at": "2019-03-19 08:49:41 PM"
//        },
//        {
//            "id": "2477fbbb-7b84-g5jb-9bkl-8741a670b0ec",
//            "title": "My Computer",
//            "body": "I sit on my computer all day.",
//            "updated_at": "2019-03-11 12:58:55 AM",
//            "created_at": "2019-01-15 11:49:41 PM"
//        },
//        {
//            "id": "247aaabb-7564-g5jb-9bkl-8741ah70b0ec",
//            "title": "Courses",
//            "body": "I make Android dev courses for a living.",
//            "updated_at": "2019-05-04 09:44:55 AM",
//            "created_at": "2019-01-15 10:11:41 PM"
//        }
//    ]


}