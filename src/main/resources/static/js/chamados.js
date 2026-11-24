const mensagem = document.getElementById('mensagem');
const dialog = document.getElementById('resolucao');
let chamadoId;
async function enviar(url, method, form) {
    const button = form.querySelector('button');
    button.disabled = true;
    try {
        const response = await fetch(url, {method, headers: {
            'Content-Type': 'application/json',
            [document.querySelector('meta[name="csrf-header"]').content]: document.querySelector('meta[name="csrf-token"]').content
        }, body: JSON.stringify(Object.fromEntries(new FormData(form)))});
        if (!response.ok) {
            const data = await response.json().catch(() => ({}));
            throw new Error(data.mensagem || data.detail || ({401:'Entre novamente para continuar.',403:'Você não tem permissão ou sua sessão expirou.',409:'Este chamado já foi alterado. Atualize a página.'})[response.status] || 'Não foi possível salvar. Confira os dados.');
        }
        location.reload();
    } catch (error) {
        dialog.close();
        mensagem.className = 'alert alert-danger';
        mensagem.textContent = error.message;
    } finally { button.disabled = false; }
}
document.getElementById('abrir').addEventListener('submit', event => {
    event.preventDefault(); enviar('api/chamados', 'POST', event.currentTarget);
});
document.querySelectorAll('.resolver').forEach(button => button.addEventListener('click', () => {
    chamadoId = button.dataset.id; document.getElementById('resolver').reset(); dialog.showModal();
}));
document.getElementById('fechar').addEventListener('click', () => dialog.close());
document.getElementById('resolver').addEventListener('submit', event => {
    event.preventDefault(); enviar('api/chamados/' + encodeURIComponent(chamadoId) + '/resolver', 'PUT', event.currentTarget);
});
