// 定数宣言: APIサーバーのURLを定義します。
// Dockerコンテナ内のSpring Bootアプリはポート8080で動いていますが、
// docker-compose.ymlでホストのポート2222に転送しているため、ブラウザからはlocalhost:2222でアクセスします。
const API_URL = 'http://localhost:2222/api/articles';

// HTMLから操作したい要素を取得します。
// getElementByIdは、HTML内の特定のIDを持つ要素を見つけて、JavaScriptで使えるようにする命令です。
const articleList = document.getElementById('article-list'); // 記事一覧を表示するための<ul>要素
const addForm = document.getElementById('add-article-form');   // 記事を追加するための<form>要素

//非同期関数: サーバーから記事一覧を取得して、HTMLに表示するための関数です。
// asyncキーワードは、この関数が非同期処理（待つことができる処理）を含むことを示します。
async function fetchArticles() {
    // try...catch構文: エラーが発生する可能性のある処理を安全に実行するための仕組みです。
    try {
        // fetch関数: API_URLに対してHTTPリクエストを送信し、サーバーからの応答を待ちます。
        // awaitキーワードは、非同期処理が終わるまでここで待つ、という命令です。
        const response = await fetch(API_URL);
        // response.json(): サーバーからの応答（JSON形式の文字列）をJavaScriptのオブジェクトに変換します。
        const articles = await response.json();

        // 記事一覧を一度空にします。これをしないと、表示を更新するたびに記事が重複してしまいます。
        articleList.innerHTML = '';
        // forEachループ: 取得した記事の配列を一つずつ処理します。
        articles.forEach(article => {
            // document.createElement('li'): 新しい<li>要素（リストの項目）を作成します。
            const li = document.createElement('li');
            // li.textContent: <li>要素の中に表示するテキストを設定します。
            // 例: "[1] はじめての投稿: こんにちは"
            li.textContent = `[${article.id}] ${article.title}: ${article.content}`;
            // articleList.appendChild(li): 作成した<li>要素を、HTMLの<ul>要素の中に追加します。
            articleList.appendChild(li);
        });
    } catch (error) {
        // もしtryブロック内でエラーが発生した場合、このcatchブロックが実行されます。
        console.error('記事の取得に失敗しました:', error);
    }
}

// イベントリスナー: 記事投稿フォームで「投稿」ボタンが押された（submitイベントが発生した）ときに実行される処理を定義します。
addForm.addEventListener('submit', async (e) => {
    // e.preventDefault(): フォームが持つデフォルトの送信動作（ページのリロード）をキャンセルします。
    // これをしないと、ページが再読み込みされてしまい、JavaScriptでの処理が中断されます。
    e.preventDefault();

    // フォームに入力された値を取得します。
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;

    try {
        // fetch関数を使って、サーバーに新しい記事のデータを送信（POSTリクエスト）します。
        await fetch(API_URL, {
            method: 'POST', // HTTPメソッドとしてPOSTを指定
            headers: {
                // 送信するデータの内容がJSON形式であることをサーバーに伝えます。
                'Content-Type': 'application/json',
            },
            // JavaScriptのオブジェクトをJSON形式の文字列に変換して、リクエストの本体（body）として送信します。
            body: JSON.stringify({ title, content }),
        });

        // addForm.reset(): 投稿後、フォームの入力欄を空にします。
        addForm.reset();
        // fetchArticles(): 新しい記事が追加されたので、記事一覧を再読み込みして表示を更新します。
        fetchArticles();
    } catch (error) {
        // POSTリクエストが失敗した場合の処理です。
        console.error('記事の投稿に失敗しました:', error);
    }
});

// 初期表示: このJavaScriptファイルが読み込まれたときに、最初に一度だけ実行されます。
// これにより、ページを開いたときにすぐ記事一覧が表示されます。
fetchArticles();