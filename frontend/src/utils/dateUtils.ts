/**
 * Utilitários para formatação e manipulação de datas
 * Seguindo princípios de Clean Code com responsabilidade única
 */

/**
 * Formata uma data para o padrão brasileiro (dd/mm/aaaa)
 * Trata diferentes formatos de entrada: string ISO, array de números, timestamp
 * 
 * @param dateInput String da data, array de números ou timestamp
 * @returns String formatada no padrão brasileiro ou "Data inválida"
 */
export const formatDateToBrazilian = (dateInput: string | number[] | number | null | undefined): string => {
  if (!dateInput) {
    console.warn('formatDateToBrazilian: dateInput é null ou undefined');
    return 'Data inválida';
  }

  console.log('formatDateToBrazilian: formatando data:', dateInput, 'tipo:', typeof dateInput);
  
  // Teste específico para o formato da API
  if (Array.isArray(dateInput) && dateInput.length === 7) {
    console.log('formatDateToBrazilian: detectado formato de array da API:', dateInput);
    console.log('formatDateToBrazilian: ano:', dateInput[0], 'mês:', dateInput[1], 'dia:', dateInput[2]);
  }

  try {
    let date: Date;

    // Trata formato de array [ano, mês, dia, hora, minuto, segundo, nanosegundos]
    if (Array.isArray(dateInput)) {
      console.log('formatDateToBrazilian: processando array de números:', dateInput);
      
      if (dateInput.length >= 6) {
        // Array: [ano, mês, dia, hora, minuto, segundo, nanosegundos]
        const [year, month, day, hour, minute, second] = dateInput;
        
        // Mês no array é 0-based, então subtrai 1
        // Validar se todos os valores são números
        if (typeof year === 'number' && typeof month === 'number' && typeof day === 'number' &&
            typeof hour === 'number' && typeof minute === 'number' && typeof second === 'number') {
          date = new Date(year, month - 1, day, hour, minute, second);
        } else {
          console.warn('Valores do array não são números válidos:', dateInput);
          return 'Data inválida';
        }
        
        console.log('formatDateToBrazilian: data criada a partir do array:', date);
      } else {
        console.warn('formatDateToBrazilian: array com formato inválido:', dateInput);
        return 'Data inválida';
      }
    } 
    // Trata timestamp (número)
    else if (typeof dateInput === 'number') {
      console.log('formatDateToBrazilian: processando timestamp:', dateInput);
      date = new Date(dateInput);
    }
    // Trata string
    else {
      console.log('formatDateToBrazilian: processando string:', dateInput);
      date = new Date(dateInput);
    }
    
    // Verifica se a data é válida
    if (isNaN(date.getTime())) {
      console.warn('formatDateToBrazilian: data inválida:', dateInput);
      return 'Data inválida';
    }
    
    // Formata para o padrão brasileiro
    const formatted = date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
    
    console.log('formatDateToBrazilian: data formatada:', formatted);
    return formatted;
  } catch (error) {
    console.warn('Erro ao formatar data:', dateInput, error);
    return 'Data inválida';
  }
};

/**
 * Formata uma data para o padrão brasileiro com hora (dd/mm/aaaa hh:mm)
 * Trata diferentes formatos de entrada: string ISO, array de números, timestamp
 * 
 * @param dateInput String da data, array de números ou timestamp
 * @returns String formatada no padrão brasileiro com hora ou "Data inválida"
 */
export const formatDateTimeToBrazilian = (dateInput: string | number[] | number | null | undefined): string => {
  if (!dateInput) {
    return 'Data inválida';
  }

  try {
    let date: Date;

    // Trata formato de array [ano, mês, dia, hora, minuto, segundo, nanosegundos]
    if (Array.isArray(dateInput)) {
      if (dateInput.length >= 6) {
        // Array: [ano, mês, dia, hora, minuto, segundo, nanosegundos]
        const [year, month, day, hour, minute, second] = dateInput;
        
        // Mês no array é 0-based, então subtrai 1
        // Validar se todos os valores são números
        if (typeof year === 'number' && typeof month === 'number' && typeof day === 'number' &&
            typeof hour === 'number' && typeof minute === 'number' && typeof second === 'number') {
          date = new Date(year, month - 1, day, hour, minute, second);
        } else {
          console.warn('Valores do array não são números válidos:', dateInput);
          return 'Data inválida';
        }
      } else {
        return 'Data inválida';
      }
    } 
    // Trata timestamp (número)
    else if (typeof dateInput === 'number') {
      date = new Date(dateInput);
    }
    // Trata string
    else {
      date = new Date(dateInput);
    }
    
    // Verifica se a data é válida
    if (isNaN(date.getTime())) {
      return 'Data inválida';
    }
    
    // Formata para o padrão brasileiro com hora
    return date.toLocaleString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (error) {
    console.warn('Erro ao formatar data e hora:', dateInput, error);
    return 'Data inválida';
  }
};

/**
 * Verifica se uma data é válida
 * 
 * @param dateInput String da data, array de números ou timestamp
 * @returns true se a data é válida, false caso contrário
 */
export const isValidDate = (dateInput: string | number[] | number | null | undefined): boolean => {
  if (!dateInput) {
    return false;
  }

  try {
    let date: Date;

    if (Array.isArray(dateInput)) {
      if (dateInput.length >= 6) {
        const [year, month, day, hour, minute, second] = dateInput;
        // Validar se todos os valores são números
        if (typeof year === 'number' && typeof month === 'number' && typeof day === 'number' &&
            typeof hour === 'number' && typeof minute === 'number' && typeof second === 'number') {
          date = new Date(year, month - 1, day, hour, minute, second);
        } else {
          console.warn('Valores do array não são números válidos:', dateInput);
          return false; // isValidDate retorna boolean
        }
      } else {
        return false;
      }
    } else if (typeof dateInput === 'number') {
      date = new Date(dateInput);
    } else {
      date = new Date(dateInput);
    }
    
    return !isNaN(date.getTime());
  } catch (error) {
    return false;
  }
};

/**
 * Converte uma data para um objeto Date válido
 * Trata diferentes formatos de entrada: string ISO, array de números, timestamp
 * 
 * @param dateInput String da data, array de números ou timestamp
 * @returns Objeto Date válido ou null se inválido
 */
export const parseDate = (dateInput: string | number[] | number | null | undefined): Date | null => {
  if (!dateInput) {
    return null;
  }

  try {
    let date: Date;

    if (Array.isArray(dateInput)) {
      if (dateInput.length >= 6) {
        const [year, month, day, hour, minute, second] = dateInput;
        // Validar se todos os valores são números
        if (typeof year === 'number' && typeof month === 'number' && typeof day === 'number' &&
            typeof hour === 'number' && typeof minute === 'number' && typeof second === 'number') {
          date = new Date(year, month - 1, day, hour, minute, second);
        } else {
          console.warn('Valores do array não são números válidos:', dateInput);
          return null; // parseDate retorna Date | null
        }
      } else {
        return null;
      }
    } else if (typeof dateInput === 'number') {
      date = new Date(dateInput);
    } else {
      date = new Date(dateInput);
    }
    
    if (isNaN(date.getTime())) {
      return null;
    }
    
    return date;
  } catch (error) {
    console.warn('Erro ao fazer parse da data:', dateInput, error);
    return null;
  }
};

/**
 * Formata uma data relativa (ex: "há 2 dias", "ontem", "hoje")
 * 
 * @param dateInput String da data, array de números ou timestamp
 * @returns String com a data relativa ou "Data inválida"
 */
export const formatRelativeDate = (dateInput: string | number[] | number | null | undefined): string => {
  if (!dateInput) {
    return 'Data inválida';
  }

  try {
    const date = parseDate(dateInput);
    
    if (!date) {
      return 'Data inválida';
    }
    
    const now = new Date();
    const diffInMs = now.getTime() - date.getTime();
    const diffInDays = Math.floor(diffInMs / (1000 * 60 * 60 * 24));
    
    if (diffInDays === 0) {
      return 'Hoje';
    } else if (diffInDays === 1) {
      return 'Ontem';
    } else if (diffInDays < 7) {
      return `Há ${diffInDays} dias`;
    } else if (diffInDays < 30) {
      const weeks = Math.floor(diffInDays / 7);
      return `Há ${weeks} semana${weeks > 1 ? 's' : ''}`;
    } else {
      return formatDateToBrazilian(dateInput);
    }
  } catch (error) {
    console.warn('Erro ao formatar data relativa:', dateInput, error);
    return 'Data inválida';
  }
};
