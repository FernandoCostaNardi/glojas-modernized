import { useState, useEffect } from 'react';

/**
 * Interface para os breakpoints de responsividade
 */
interface MediaQueryBreakpoints {
  readonly mobile: number;
  readonly tablet: number;
  readonly desktop: number;
  readonly largeDesktop: number;
}

/**
 * Interface para o resultado do hook useMediaQuery
 */
export interface MediaQueryResult {
  readonly isMobile: boolean;
  readonly isTablet: boolean;
  readonly isDesktop: boolean;
  readonly isLargeDesktop: boolean;
  readonly width: number;
  readonly height: number;
}

/**
 * Breakpoints baseados no Tailwind CSS
 */
const BREAKPOINTS: MediaQueryBreakpoints = {
  mobile: 640,    // sm
  tablet: 768,    // md
  desktop: 1024,  // lg
  largeDesktop: 1280, // xl
} as const;

/**
 * Hook personalizado para detectar tamanho da tela e dispositivo
 * Implementa padrão mobile-first com breakpoints responsivos
 * 
 * @returns Objeto com informações sobre o dispositivo atual
 */
export const useMediaQuery = (): MediaQueryResult => {
  const [windowSize, setWindowSize] = useState<{ width: number; height: number }>({
    width: typeof window !== 'undefined' ? window.innerWidth : 0,
    height: typeof window !== 'undefined' ? window.innerHeight : 0,
  });

  useEffect(() => {
    /**
     * Manipula mudanças no tamanho da janela
     */
    const handleResize = (): void => {
      setWindowSize({
        width: window.innerWidth,
        height: window.innerHeight,
      });
    };

    /**
     * Função debounced para otimizar performance
     */
    let timeoutId: ReturnType<typeof setTimeout> | undefined;
    const debouncedHandleResize = (): void => {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
      timeoutId = setTimeout(handleResize, 150);
    };

    // Adiciona listener apenas no lado cliente
    if (typeof window !== 'undefined') {
      window.addEventListener('resize', debouncedHandleResize);
      
      // Cleanup
      return () => {
        window.removeEventListener('resize', debouncedHandleResize);
        if (timeoutId) {
          clearTimeout(timeoutId);
        }
      };
    }
    
    // Retorno vazio para SSR
    return () => {};
  }, []);

  /**
   * Calcula os estados do dispositivo baseado na largura atual
   */
  const isMobile = windowSize.width < BREAKPOINTS.mobile;
  const isTablet = windowSize.width >= BREAKPOINTS.mobile && windowSize.width < BREAKPOINTS.desktop;
  const isDesktop = windowSize.width >= BREAKPOINTS.desktop && windowSize.width < BREAKPOINTS.largeDesktop;
  const isLargeDesktop = windowSize.width >= BREAKPOINTS.largeDesktop;

  return {
    isMobile,
    isTablet,
    isDesktop,
    isLargeDesktop,
    width: windowSize.width,
    height: windowSize.height,
  };
};

/**
 * Hook utilitário para verificar se está em modo mobile
 * @returns true se estiver em dispositivo móvel
 */
export const useIsMobile = (): boolean => {
  const { isMobile } = useMediaQuery();
  return isMobile;
};

/**
 * Hook utilitário para verificar se está em modo desktop
 * @returns true se estiver em dispositivo desktop ou maior
 */
export const useIsDesktop = (): boolean => {
  const { isDesktop, isLargeDesktop } = useMediaQuery();
  return isDesktop || isLargeDesktop;
};

export default useMediaQuery;
