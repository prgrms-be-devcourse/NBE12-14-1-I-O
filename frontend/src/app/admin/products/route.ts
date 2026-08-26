export async function POST(request: Request) {
    const body = await request.json();

    const response = await fetch(`http://localhost:8080/admin/products/`,
        {
            method: "post",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body),
        }
    );
}