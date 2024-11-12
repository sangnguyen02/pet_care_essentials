const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

const db = admin.database();

exports.updateDiscountStatusDaily = functions.pubsub
    .schedule("every 24 hours")
    .onRun(async (context) => {
        const today = new Date();

        try {
            const snapshot = await db.ref("Discount").once("value");
            snapshot.forEach((discountSnapshot) => {
                const discount = discountSnapshot.val();
                const discountId = discount.discount_id;

                const startDate = new Date(discount.start_date);
                const endDate = new Date(discount.end_date);
                let newStatus;

                if (today < startDate) {
                    newStatus = "New";
                } else if (today >= startDate && today <= endDate) {
                    newStatus = "Active";
                } else if (today > endDate) {
                    newStatus = "Disable";
                }

                // Cập nhật trạng thái nếu khác trạng thái hiện tại
                if (newStatus && newStatus !== discount.status) {
                    db.ref(`Discount/${discountId}/status`).set(newStatus);
                }
            });

            console.log("Discount status updated successfully!");
        } catch (error) {
            console.error("Error updating discount status:", error);
        }
    });
