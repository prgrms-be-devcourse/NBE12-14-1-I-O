export type Order = {
    orderId: number;
    orderedAt: string;
    orderStatus: OrderStatus;
    email: string;
    address: string;
    postalCode: string;
    price: number;
    deliveryStatus: DeliveryStatus;
    createdAt: string;
    shippedAt: string | null;
    deliveredAt: string | null;
    orderItemResponses: OrderItem[];
};

export type OrderItem = {
    orderItemId: number;
    name: string;
    quantity: number;
    price: number;
    imageFilename: string;
};
export type OrderStatus = "ORDERED" | "CANCELED";

export type DeliveryStatus =
    | "ORDERED"
    | "SHIPPING"
    | "DELIVERED"
    | "CANCELLED";