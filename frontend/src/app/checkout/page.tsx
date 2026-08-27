"use client";

import { useEffect, useState } from "react";

type CartItem = {
    id: number;
    name: string;
    price: number;
    quantity: number;
  };

export default function CheckoutPage() {

    const [cartItems, setCartItems] = useState<CartItem[]>([]);
    const [email, setEmail] = useState("");
    const [address, setAddress] = useState("");
    const [postalCode, setPostalCode] = useState("");

    useEffect(() => {
        const savedCart = localStorage.getItem("cart");
      
        if (savedCart) {
          const parsedCart: CartItem[] = JSON.parse(savedCart);
      
          setCartItems(parsedCart);
        }
    }, []);

    const totalPrice = cartItems.reduce(
        (sum, item) => sum + item.price * item.quantity,
        0
      );

    return (
      <main
        className="
          mx-auto mt-8
          flex max-w-7xl
          gap-12
          rounded-[40px]
          bg-white
          p-12
        "
      >
        {/* 왼쪽 주문 정보 입력 */}
        <section
          className="
            flex-1
            rounded-xl
            border border-neutral-300
            bg-[#fffaf0]
            p-8
          "
        >
          {/* Email */}
          <div>
            <label className="text-lg font-bold">
              Email
            </label>
  
            <input
            value={email}
            onChange={(e) => {
                setEmail(e.target.value);
              }}
              className="
                mt-2 w-full
                rounded-md
                border border-neutral-300
                bg-white
                p-3
                outline-none
              "
              placeholder="example@email.com"
            />
          </div>
  
          {/* 주소 */}
          <div className="mt-6">
            <label className="text-lg font-bold">
              주소
            </label>
  
            <input
            value={address}
            onChange={(e) => {
                setAddress(e.target.value);
              }}
              className="
                mt-2 w-full
                rounded-md
                border border-neutral-300
                bg-white
                p-3
                outline-none
              "
              placeholder="서울시 강남구"
            />
          </div>
  
          {/* 우편번호 */}
          <div className="mt-6">
            <label className="text-lg font-bold">
              우편번호
            </label>
  
            <input
            value={postalCode}
            onChange={(e) => {
                setPostalCode(e.target.value);
              }}
              className="
                mt-2 w-full
                rounded-md
                border border-neutral-300
                bg-white
                p-3
                outline-none
              "
              placeholder="12345"
            />
          </div>
  
          <p className="mt-4 text-sm text-neutral-500">
            당일 오후 2시 이후의 주문은 다음 날 배송을 시작합니다.
          </p>
  
          <button
          onClick={async () => {
            const items = cartItems.map((item) => ({
              productId: item.id,
              quantity: item.quantity,
            }));

            const requestBody = {
                email,
                address,
                postalCode,
                items,
              };

              const response = await fetch("http://localhost:8080/orders", {
                method: "POST",
                headers: {
                  "Content-Type": "application/json",
                },
                body: JSON.stringify(requestBody),
              });

              console.log("status:", response.status);
              console.log("ok:", response.ok);
          
              const result = await response.json();
          
              console.log("result:", result);
            }}

            className="
              mt-5 w-full
              rounded-md
              bg-neutral-800
              py-3
              text-white
            "
          >
            결제
          </button>
        </section>
  
        {/* 오른쪽 Summary */}
        <aside
          className="
            w-80
            self-start
            rounded-xl
            border border-neutral-300
            bg-[#fffaf0]
            p-7
          "
        >
          <h2 className="text-4xl">
            Summary
          </h2>
  
          <hr className="my-5 border-neutral-400" />
  
          <div className="space-y-4">
            {cartItems.map((item) => (
                <div
                key={item.id}
                className="flex justify-between"
                >
                    <span>
                    {item.name} × {item.quantity}
                    </span>

                    <span>
                    {(item.price * item.quantity).toLocaleString()}원
                    </span>
                </div>
                ))}
          </div>
  
          <hr className="my-6 border-neutral-400" />
  
          <div className="flex justify-between text-lg">
            <span>총 금액</span>
            <span>{totalPrice.toLocaleString()}원</span>
          </div>
        </aside>
      </main>
    );
  }