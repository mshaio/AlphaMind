package com.example.alphamind
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId

open class ExerciseModel(var activity: String = "",
                         var exerciseType: String = "",
                         @PrimaryKey
                         var _id: ObjectId = ObjectId(),
                         var sets: Int = 0,
                         var reps: Int = 0,
                         var weights: Int = 0,
                         var date: String? = null,
                         var notes: String? = null): RealmObject()


//class ExerciseModel {
//}