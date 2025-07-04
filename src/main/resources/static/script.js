const API_URL = 'http://localhost:2222/api/articles'; // デプロイ後は本番URLに変更

const articleList = document.getElementById('article-list');
const addForm = document.getElementById('add-article-form');

// 記事一覧を取得して表示する関数
async function fetchArticles() {
    try {
        const response = await fetch(API_URL);
        const articles = await response.json();

        articleList.innerHTML = ''; // 一旦リストを空にする
        articles.forEach(article => {
            const li = document.createElement('li');
            li.textContent = `[${article.id}] ${article.title}: ${article.content}`;
            articleList.appendChild(li);
        });
    } catch (error) {
        console.error('記事の取得に失敗しました:', error);
    }
}

// 記事を投稿する関数
addForm.addEventListener('submit', async (e) => {
    e.preventDefault(); // フォームのデフォルト送信を防ぐ

    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;

    try {
        await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ title, content }),
        });

        addForm.reset(); // フォームをリセット
        fetchArticles(); // 記事一覧を再読み込み
    } catch (error) {
        console.error('記事の投稿に失敗しました:', error);
    }
});

// ページ読み込み時に記事一覧を初回取得
fetchArticles();