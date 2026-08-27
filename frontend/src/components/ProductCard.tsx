import { Product } from '@/types/product';

interface ProductCardProps {
    product: Product
}

export default function ProductCard({ product }: ProductCardProps) {
    return (
        <article
            className="rounded-lg
                  border border-neutral-300
                  bg-white
                  p-4"
        >
            <img
                className="w-full aspect-square rounded bg-neutral-100 object-cover"
                src={`http://localhost:8080/${product.imageFileUrl}`}
                alt="상품 이미지" />

            <h3 className="mt-4 font-bold">
                {product.name}
            </h3>

            <p>
                {product.price.toLocaleString()}원
            </p>

            <p>
                {product.stock}개
            </p>

            <div className="mt-4 flex gap-2">
                <button className="flex-1 rounded bg-neutral-600 py-2 text-white">
                    수정
                </button>

                <button className="flex-1 rounded bg-red-500 py-2 text-white">
                    삭제
                </button>
            </div>
        </article>
    );
}