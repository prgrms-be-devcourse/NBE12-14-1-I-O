import { Product } from "@/types/Product";

interface ProductCardUserProps {
    product: Product;
    quantities: number;
    handleClickDecrease: () => void;
    handleClickIncrease: () => void;
    handleClickCartItem: () => void;
}

export default function ProductCardUser({ product, quantities, handleClickDecrease, handleClickIncrease, handleClickCartItem }: ProductCardUserProps) {

    quantities === undefined ? quantities = 1 : undefined;

    console.log(`${process.env.NEXT_PUBLIC_API_URL}/app/data/${product.imageFileUrl}`);

    return (
        <article
            key={product.id}
            className="
                flex flex-col
                rounded-lg
                border border-neutral-400
                bg-white
                p-6
              "
        >
            <img
                className="flex aspect-square
                  items-center justify-center
                  bg-neutral-100
                  text-neutral-400"
                src={product.imageFileUrl === 'images/null' ? '/baseThumbnail.png' : `${process.env.NEXT_PUBLIC_API_URL}/app/data/${product.imageFileUrl}`}
                alt="상품 이미지" />


            {/* 상품 정보 */}
            <div className="mt-5">
                <h3 className="pr-5 text-l font-bold w-[200px] truncate">
                    {product.name}
                </h3>

                <p className="w-[200px] truncate line-clamp-3" title={product.description}>
                    {product.description}
                </p>

                <p className="text-sm font-bold">
                    남은 수량: {product.stock}
                </p>

                <p className="mt-3 text-xl font-bold">
                    {product.price.toLocaleString()}원
                </p>
            </div>

            {/* 수량 조절 + 장바구니 */}
            <div className="mt-5 flex items-center gap-4">
                {/* 수량 조절 */}
                <div
                    className="
                  flex flex-1
                  items-center justify-between
                  rounded-full
                  border border-neutral-700
                  px-1 py-1
                  "
                >
                    <button
                        type="button"
                        className="text-xl px-3 py-1"
                        onClick={handleClickDecrease}
                    >
                        -
                    </button>
                    <span className="font-bold" >
                        {quantities}
                    </span>
                    <button
                        type="button"
                        className="text-xl px-3 py-1"
                        onClick={handleClickIncrease}
                    >
                        +
                    </button>
                </div>

                {/* 장바구니 버튼 */}
                <button
                    onClick={() => {
                        if (quantities > product.stock) {
                            alert(`재고가 부족합니다.\n최대 담을 수 있는 수량: ${product.stock}개`);
                            return;
                        }

                        const cartItem: CartItem = {
                            id: product.id,
                            name: product.name,
                            price: product.price,
                            quantity: quantities ?? 1,
                            imageFileUrl: product.imageFileUrl,
                        };
                        const savedCart = localStorage.getItem("cart");

                        const cartItems: CartItem[] = savedCart
                            ? JSON.parse(savedCart)
                            : [];

                        const existingItem = cartItems.find(
                            (item) => item.id === cartItem.id
                        );

                        if (existingItem) {
                            if (existingItem.quantity === product.stock) {
                                alert(`모든 재고를 장바구니에 담았습니다.`);
                                return;
                            }

                            if (existingItem.quantity + cartItem.quantity > product.stock) {
                                alert(`재고가 부족합니다.\n최대 담을 수 있는 수량: ${product.stock - existingItem.quantity}개`);
                                return;
                            }
                            existingItem.quantity += cartItem.quantity;
                        } else {
                            cartItems.push(cartItem);
                        }

                        localStorage.setItem(
                            "cart",
                            JSON.stringify(cartItems)
                        );

                        handleClickCartItem();
                        alert(`${product.name} ${quantities}개를 장바구니에 담았습니다.`);
                    }}

                    className="
                  flex h-11 w-11
                  shrink-0
                  items-center justify-center
                  rounded-full
                  bg-[#FF902A]
                  "
                >
                    <img
                        src="/images/cart-icon.png"
                        alt="장바구니 담기"
                        className="h-5 w-5"
                    />
                </button>
            </div>
        </article>
    );
}
