import { Product } from '@/types/Product';

interface ProductCardProps {
    product: Product
    onDelete: (id: number) => void;
    onEdit: (product: Product) => void;
}

export default function ProductCard({ product, onDelete, onEdit }: ProductCardProps) {
    
    // 삭제 버튼 클릭 핸들러
    const handleDeleteClick = () => {
        if (window.confirm(`"${product.name}" 상품을 삭제하시겠습니까?`)) {
            onDelete(product.id);
        }
    };

    return (
        <article
            className="rounded-lg
                  border border-neutral-300
                  bg-white
                  p-4"
        >
            <img
                className="w-full aspect-square rounded bg-neutral-100 object-cover"
                src={product.imageFileUrl === 'images/null' ? '/baseThumbnail.png' : `http://localhost:8080/api/v1/${product.imageFileUrl}`}
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
                <button
                onClick={() => onEdit(product)}
                className="flex-1 rounded bg-neutral-600 py-2 text-white hover:bg-neutral-700 transition">
                    수정
                </button>

                <button onClick={handleDeleteClick}
                className="flex-1 rounded bg-red-500 py-2 text-white hover:bg-red-600 transition">
                    삭제
                </button>
            </div>
        </article>
    );
}