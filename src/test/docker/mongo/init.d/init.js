function createCollections(database) {
    database.createCollection("users");
}

const db = db.getSiblingDB("haylugar");

createCollections(db);

const usersCollection = db.getCollection("users");

usersCollection.insertOne({
   "email": "test@email.com"
});

