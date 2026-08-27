export type Order = {
    id: number;
    date: string;
    status: string;
    price: number;
    address: string;
    zipCode: string;
    items: OrderItem[];
}

export type OrderItem = {
    id: number;
    name: string;
    quantity: number;
    price: number;
};