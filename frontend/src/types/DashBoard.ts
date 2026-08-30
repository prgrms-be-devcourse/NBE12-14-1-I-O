type DashBoard = {
    revenueDashBoards: RevenueDashBoard[];
    soldTop3DashBoards: SoldTop3DashBoard[];
    revenueTop3DashBoards: RevenueTop3DashBoard[];
}

type RevenueDashBoard = {
    name: string;
    quantity: number;
    unitPrice: number;
    totalPrice: number;
}

type SoldTop3DashBoard = {
    name: string;
    quantity: number;
    totalPrice: number;
}

type RevenueTop3DashBoard = {
    name: string;
    quantity: number;
    totalPrice: number;
}