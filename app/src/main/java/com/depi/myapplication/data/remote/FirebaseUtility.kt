package com.depi.myapplication.data.remote

import com.depi.myapplication.data.models.Address
import com.depi.myapplication.data.models.CartProduct
import com.depi.myapplication.data.models.User
import com.depi.myapplication.data.models.order.Order
import com.depi.myapplication.util.constants.Constants.FireBaseConstants.ADDRESS_COLLECTION
import com.depi.myapplication.util.constants.Constants.FireBaseConstants.CART_COLLECTION
import com.depi.myapplication.util.constants.Constants.FireBaseConstants.CATEGORIES_COLLECTION
import com.depi.myapplication.util.constants.Constants.FireBaseConstants.ORDER_COLLECTION
import com.depi.myapplication.util.constants.Constants.FireBaseConstants.PRODUCTS_COLLECTION
import com.depi.myapplication.util.constants.Constants.FireBaseConstants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import javax.inject.Inject

class FirebaseUtility @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) {
    // TODO: This class is a firebase services collection for easy access


    private val usersCollectionRef = Firebase.firestore.collection(USER_COLLECTION)
    private val productsCollection = Firebase.firestore.collection(PRODUCTS_COLLECTION)
    private val userUid = firebaseAuth.currentUser?.uid
    private val firebaseStorage = Firebase.storage.reference


    // get reference to sub-collection
    private val userCartCollection = userUid?.let {
        fireStore.collection(USER_COLLECTION).document(it).collection(CART_COLLECTION)
    }
    private val userAddressCollection = userUid?.let {
        fireStore.collection(USER_COLLECTION).document(it).collection(ADDRESS_COLLECTION)
    }

    fun currentUser() = firebaseAuth.currentUser
    fun getUser() = fireStore.collection(USER_COLLECTION).document(firebaseAuth.uid!!)

    fun searchProduct(query: String) =
        productsCollection
            .orderBy("name")
            .startAt(query)
            .endAt("\u03A9+$query")
            .limit(10)
            .get()


    fun login(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)



    fun logout() = firebaseAuth.signOut()

    fun createNewUser(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun resetPassword(email: String) =
        firebaseAuth.sendPasswordResetEmail(email)

    fun checkUserExistence(email: String) =
        fireStore.collection(USER_COLLECTION).whereEqualTo("email", email)
            .get()

    fun saveUserData(uid: String, user: User) =
        fireStore.collection(USER_COLLECTION).document(uid).set(user)


    fun getSpecialProducts() =
        fireStore.collection(PRODUCTS_COLLECTION).whereEqualTo("category", "Special Products").get()

    fun getBestDeals() =
        fireStore.collection(PRODUCTS_COLLECTION).whereEqualTo("category", "Best Deals").get()

    fun getBestProducts(pagingLimit: Long) =
        fireStore.collection(PRODUCTS_COLLECTION).orderBy(
            "id",
            Query.Direction.ASCENDING
        ).limit(pagingLimit)
            .get()

    fun addAddress(address: Address) =
        fireStore.collection(USER_COLLECTION).document(firebaseAuth.uid!!).collection(
            ADDRESS_COLLECTION
        ).document().set(address)

    fun getAddress() =
        fireStore.collection(USER_COLLECTION).document(firebaseAuth.uid!!).collection(
            ADDRESS_COLLECTION)
    fun getCartProducts() =
        fireStore.collection(USER_COLLECTION).document(firebaseAuth.uid!!).collection(
            CART_COLLECTION
        )

    fun deleteCart(documentId: String) =
        fireStore.collection(USER_COLLECTION).document(firebaseAuth.uid!!).collection(
            CART_COLLECTION
        ).document(documentId).delete()

    fun getOfferProducts(category: String) =
        fireStore.collection(PRODUCTS_COLLECTION).whereEqualTo(CATEGORIES_COLLECTION, category)
            .whereNotEqualTo("offerPercentage", null).get()

    fun addUpdateProductInCart(cartProduct: CartProduct) =
        fireStore.collection(USER_COLLECTION).document(firebaseAuth.uid!!).collection(CART_COLLECTION)
            .whereEqualTo("product.id", cartProduct.product.id).get()

    fun getAllOrders() =
        fireStore.collection(USER_COLLECTION).document(firebaseAuth.uid!!).collection(
            ORDER_COLLECTION).get()

    fun addOrderIntoUserCollection(order: Order) =
        fireStore.collection(USER_COLLECTION)
            .document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION)
            .document()
            .set(order)

    fun addOrderIntoOrderCollection(order: Order) =
        fireStore.collection("orders").document().set(order)
}