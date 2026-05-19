/**
 * Base para todas as exceções da aplicação
 */
class AppError extends Error {
    constructor(message, status, details = null) {
        super(message);
        this.name = this.constructor.name;
        this.status = status; // Código HTTP
        this.details = details; // Objeto de erro completo do backend (timestamp, path, etc)
        this.timestamp = new Date().toISOString();
        Error.captureStackTrace(this, this.constructor);
    }
}

/** Erro 400 - Falha de validação ou corpo da requisição malformado */
class ValidationError extends AppError {
    constructor(message = 'Dados inválidos.', details = null) {
        super(message, 400, details);
    }
}

/** Erro 401 - Não autenticado */
class AuthenticationError extends AppError {
    constructor(message = 'Sessão expirada. Por favor, faça login novamente.', details = null) {
        super(message, 401, details);
    }
}

/** Erro específico para Login - E-mail ou Senha incorretos */
class InvalidCredentialsError extends AuthenticationError {
    constructor(message = 'E-mail ou senha incorretos. Por favor, verifique seus dados.', details = null) {
        super(message, details);
    }
}

/** Erro 403 - Sem permissão */
class ForbiddenError extends AppError {
    constructor(message = 'Você não tem permissão para realizar esta ação.', details = null) {
        super(message, 403, details);
    }
}

/** Erro 404 - Não encontrado */
class NotFoundError extends AppError {
    constructor(message = 'Recurso não encontrado.', details = null) {
        super(message, 404, details);
    }
}

/** Erro 409 - Conflito (ex: e-mail já existe) */
class ConflictError extends AppError {
    constructor(message, details = null) {
        super(message, 409, details);
    }
}

/** Erro 500 - Erro interno do servidor */
class InternalServerError extends AppError {
    constructor(message = 'Ocorreu um erro interno no servidor.', details = null) {
        super(message, 500, details);
    }
}

/**
 * Função global para tratar exceções da aplicação e exibir feedback ao usuário.
 * Centraliza a lógica de 'instanceof' que antes ficava espalhada nos catchs.
 */
function handleAppError(error) {
    console.error('Erro capturado:', error);

    if (error instanceof InvalidCredentialsError) {
        alert(`❌ Falha no Login: ${error.message}`);
    } else if (error instanceof ValidationError) {
        alert(`⚠️ Dados Inválidos: ${error.message}`);
    } else if (error instanceof ConflictError) {
        alert(`🛑 Conflito: ${error.message}`);
    } else if (error instanceof NotFoundError) {
        console.error(`🔍 Recurso não encontrado: ${error.message}`);
        alert(`Erro 404: O sistema não encontrou os dados solicitados. Verifique se o item não foi excluído.`);
    } else if (error instanceof ForbiddenError) {
        alert(`🚫 Acesso Negado: ${error.message}`);
    } else if (error instanceof AuthenticationError) {
        // Geralmente o query.js já redireciona, mas o alerta confirma o motivo
        alert(`🔑 Sessão Expirada: ${error.message}`);
    } else if (error instanceof InternalServerError) {
        alert(`💥 Erro no Servidor: ${error.message}`);
    } else if (error instanceof AppError) {
        alert(`⚙️ Erro na Aplicação: ${error.message}`);
    } else {
        // Fallback para erros genéricos do JavaScript (como TypeError)
        alert(`❌ Erro Inesperado: ${error.message || 'Ocorreu um problema desconhecido.'}`);
    }
}

export { 
    InternalServerError,
    ConflictError,
    NotFoundError,
    ForbiddenError,
    AuthenticationError,
    InvalidCredentialsError,
    ValidationError,
    AppError,
    handleAppError
}