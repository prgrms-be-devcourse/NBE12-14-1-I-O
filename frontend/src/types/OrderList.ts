
export type OrderList = {
    orderId: number;
    orderedAt: string;
    orderStatus: OrderStatus;
    deliveryStatus: DeliveryStatus;
    address: string;
    postalCode: string;
    price: number
    orderItemResponses: OrderItem[];
}

export type OrderItem = {
    name: string;
    quantity: number;
    price: number;
    imageFilename: string;
}

export type OrderStatus =
    | "ORDERED"
    | "CANCELED";

export type DeliveryStatus =
    | "ORDERED"
    | "SHIPPING"
    | "DELIVERED"
    | "CANCELLED";

