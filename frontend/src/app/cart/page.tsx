"use client";

import { useEffect, useState } from "react";
import Link from "next/link";

type CartItem = {
  id: number;
  name: string;
  price: number;
  quantity: number;
  imageFileUrl: string;
};


export default function CartPage() {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);

  useEffect(() => {
    const savedCart = localStorage.getItem("cart");

    if (savedCart) {
      const parsedCart: CartItem[] = JSON.parse(savedCart);

      setCartItems(parsedCart);
    }
  }, []);

  const totalQuantity = cartItems.reduce(
    (sum, item) => sum + item.quantity,
    0
  );

  const totalPrice = cartItems.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  return (
    <main
      className="
        mx-auto mt-8
        flex max-w-7xl gap-10
        rounded-[40px]
        bg-white
        p-12
      "
    >
      {/* 왼쪽 상품 목록 */}
      <section className="flex-1 space-y-6">
        {cartItems.map((item) => (
          <article
            key={item.id}
            className="
              flex items-center
              rounded-xl
              border border-neutral-300
              bg-[#fffaf0]
              p-5
            "
          >
            {/* 이미지 */}
            <img
              className="w-24 h-24 flex aspect-square
                  items-center justify-center
                  bg-neutral-100
                  text-neutral-400"
              src={item.imageFileUrl === 'images/null' ? '/baseThumbnail.png' : `http://localhost:8080/api/v1/${item.imageFileUrl}`}
              alt="상품 이미지" />

            {/* 상품 정보 */}
            <div className="ml-6 flex-1">
              <h2 className="text-xl font-bold">
                {item.name}
              </h2>

              <p className="mt-5 text-base">
                {item.price.toLocaleString()}원 × {item.quantity}
              </p>
            </div>

            <div className="flex items-center gap-3">
              {/* 수량 */}
              <div
                className="
                flex w-40
                items-center justify-between
                rounded-full
                border border-neutral-700
                px-5 py-3
              "
              >
                <button
                  className="text-xl"
                  onClick={() => {
                    const updatedCartItems = cartItems.map((cartItem) =>
                      cartItem.id === item.id
                        ? {
                          ...cartItem,
                          quantity: Math.max(cartItem.quantity - 1, 1),
                        }
                        : cartItem
                    );
                    setCartItems(updatedCartItems);

                    localStorage.setItem(
                      "cart",
                      JSON.stringify(updatedCartItems)
                    );
                  }}
                >
                  -
                </button>

                <span className="font-bold">
                  {item.quantity}
                </span>

                <button
                  className="text-xl"
                  onClick={() => {
                    const updatedCartItems = cartItems.map((cartItem) =>
                      cartItem.id === item.id
                        ? { ...cartItem, quantity: cartItem.quantity + 1 }
                        : cartItem);
                    setCartItems(updatedCartItems);

                    localStorage.setItem(
                      "cart",
                      JSON.stringify(updatedCartItems)
                    );
                  }}
                >
                  +
                </button>
              </div>

              {/* 삭제 */}
              <button
                onClick={() => {
                  const updatedCartItems = cartItems.filter(
                    (cartItem) => cartItem.id !== item.id
                  );

                  setCartItems(updatedCartItems);

                  localStorage.setItem(
                    "cart",
                    JSON.stringify(updatedCartItems)
                  );
                }}
                className="
                      rounded-md
                      bg-red-500
                      px-4 py-3
                      text-white
                      "
              >
                삭제
              </button>
            </div>
          </article>
        ))}
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

        <div className="flex justify-between text-lg">
          <span>상품 수량</span>
          <span>{totalQuantity}개</span>
        </div>

        <div className="mt-6 flex justify-between text-lg">
          <span>총 금액</span>
          <span>{totalPrice.toLocaleString()}원</span>
        </div>

        <Link
          href="/checkout"
          className="
            mt-8 block
            w-full
            rounded-md
            bg-neutral-800
            py-3
            text-center
            text-white
          "
        >
          결제하러 가기
        </Link>
      </aside>
    </main>
  );
}