package lyc.compiler.tercetos;

import java.util.*;

/**
 * Eliminador de código muerto para tercetos.
 * Analiza los tercetos generados y elimina aquellos que no contribuyen
 * al resultado final del programa (no afectan I/O o control de flujo).
 */
public class DeadCodeEliminator {

    /**
     * Elimina tercetos muertos de la lista proporcionada.
     * @param tercetos Lista original de tercetos
     * @return Nueva lista con solo tercetos vivos, re-indexados
     */
    public static List<Terceto> eliminate(List<Terceto> tercetos) {
        if (tercetos.isEmpty()) {
            return tercetos;
        }

        // Paso 1: Identificar tercetos vivos
        Set<Integer> liveIndices = findLiveTercetos(tercetos);

        // Paso 2: Crear lista optimizada
        List<Terceto> optimized = new ArrayList<>();
        Map<Integer, Integer> oldToNewIndex = new HashMap<>();

        for (int i = 0; i < tercetos.size(); i++) {
            if (liveIndices.contains(i)) {
                oldToNewIndex.put(i, optimized.size());
                optimized.add(tercetos.get(i));
            }
        }

        // Paso 3: Actualizar referencias en tercetos optimizados
        updateReferences(optimized, oldToNewIndex);

        return optimized;
    }

    /**
     * Identifica qué tercetos están vivos (afectan el resultado del programa).
     * Usa análisis de uso-def propagando hacia atrás desde puntos de observación.
     */
    private static Set<Integer> findLiveTercetos(List<Terceto> tercetos) {
        Set<Integer> live = new HashSet<>();
        Queue<Integer> worklist = new LinkedList<>();

        // Paso 1: Marcar puntos de observación (siempre vivos)
        for (int i = 0; i < tercetos.size(); i++) {
            Terceto t = tercetos.get(i);
            String op = t.getOp();

            // Efectos externos: I/O y control de flujo
            if (isObservationPoint(op)) {
                live.add(i);
                worklist.add(i);
            }
        }

        // Paso 2: Si no hay puntos de observación, marcar la última asignación a cada variable como viva
        if (live.isEmpty()) {
            Map<String, Integer> lastAssignment = new HashMap<>();
            for (int i = 0; i < tercetos.size(); i++) {
                Terceto t = tercetos.get(i);
                if (":=".equals(t.getOp()) && t.getArg1() != null) {
                    // Es una asignación, recordar la última posición
                    lastAssignment.put(t.getArg1(), i);
                }
            }
            // Marcar las últimas asignaciones como vivas
            for (int index : lastAssignment.values()) {
                live.add(index);
                worklist.add(index);
            }
        }

        // Paso 3: Propagar dependencias hacia atrás
        while (!worklist.isEmpty()) {
            int currentIndex = worklist.poll();
            Terceto current = tercetos.get(currentIndex);

            // Analizar argumentos del terceto actual
            String arg1 = current.getArg1();
            String arg2 = current.getArg2();

            // Si arg1 es una referencia [j], marcar [j] como vivo
            addDependency(arg1, live, worklist, tercetos);

            // Si arg2 es una referencia [j], marcar [j] como vivo
            addDependency(arg2, live, worklist, tercetos);
        }

        return live;
    }

    /**
     * Verifica si un operador representa un punto de observación
     * (afecta el estado externo del programa).
     */
    private static boolean isObservationPoint(String op) {
        return "READ".equals(op) || "WRITE".equals(op) ||
               "BF".equals(op) || "BI".equals(op);
    }

    /**
     * Si el argumento es una referencia a terceto [j], la marca como viva
     * y la agrega a la lista de trabajo.
     */
    private static void addDependency(String arg, Set<Integer> live,
                                    Queue<Integer> worklist, List<Terceto> tercetos) {
        if (arg != null && arg.startsWith("[")) {
            try {
                int refIndex = Integer.parseInt(arg.substring(1, arg.length() - 1));
                if (refIndex >= 0 && refIndex < tercetos.size() && !live.contains(refIndex)) {
                    live.add(refIndex);
                    worklist.add(refIndex);
                }
            } catch (NumberFormatException e) {
                // Referencia malformada, ignorar
            }
        }
    }

    /**
     * Actualiza todas las referencias [viejo] a [nuevo] en los tercetos optimizados.
     */
    private static void updateReferences(List<Terceto> optimized, Map<Integer, Integer> oldToNewIndex) {
        for (Terceto t : optimized) {
            // Actualizar arg1 si es referencia
            t.setArg1(updateReference(t.getArg1(), oldToNewIndex));

            // Actualizar arg2 si es referencia
            t.setArg2(updateReference(t.getArg2(), oldToNewIndex));
        }
    }

    /**
     * Actualiza una sola referencia usando el mapa de re-indexación.
     */
    private static String updateReference(String arg, Map<Integer, Integer> oldToNewIndex) {
        if (arg != null && arg.startsWith("[")) {
            try {
                int oldIndex = Integer.parseInt(arg.substring(1, arg.length() - 1));
                Integer newIndex = oldToNewIndex.get(oldIndex);
                if (newIndex != null) {
                    return "[" + newIndex + "]";
                }
            } catch (NumberFormatException e) {
                // Referencia malformada, devolver como está
            }
        }
        return arg;
    }
}