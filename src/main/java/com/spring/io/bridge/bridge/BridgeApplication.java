package com.spring.io.bridge.bridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
public class BridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BridgeApplication.class, args);
    }

    private static boolean iAmBeingHumillated(ArenaUpdate arenaUpdate) {
        return arenaUpdate.arena.state.get(arenaUpdate._links.self.href).wasHit
                && arenaUpdate.arena.state.get(arenaUpdate._links.self.href).score < -50;
    }

    private static boolean fightPossible(BridgeApplication.ArenaUpdate arenaUpdate) {
        switch (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction) {
            case "N":
                return checkEnemiesDirectionY(arenaUpdate, -3);
            case "W":
                return checkEnemiesDirectionX(arenaUpdate, -3);
            case "E":
                return checkEnemiesDirectionX(arenaUpdate, 3);
            case "S":
                return checkEnemiesDirectionY(arenaUpdate, 3);
            default:
                return false;

        }

    }

    private static boolean checkEnemiesDirectionX(BridgeApplication.ArenaUpdate arenaUpdate, int actionRatio) {
        Iterator<Map.Entry<String, BridgeApplication.PlayerState>> iterator = arenaUpdate.arena.state.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, BridgeApplication.PlayerState> e = iterator.next();
            String s = e.getKey();
            BridgeApplication.PlayerState p = e.getValue();
            if (!s.equals(arenaUpdate._links.self.href)
                    && p.y.equals(arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y)) {
                if (actionRatio < 0) {
                    if (p.x >= (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x + actionRatio)
                            && p.x < arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x)
                        return true;
                } else {
                    if (p.x > arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x
                            && (p.x <= arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x + actionRatio))
                        return true;
                }

            }
        }
        return false;
    }

    private static boolean checkEnemiesDirectionY(BridgeApplication.ArenaUpdate arenaUpdate, int actionRatio) {

        for (Map.Entry<String, PlayerState> e : arenaUpdate.arena.state.entrySet()) {
            String s = e.getKey();
            PlayerState p = e.getValue();
            if (!s.equals(arenaUpdate._links.self.href)
                    && p.x.equals(arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x)) {
                if (actionRatio < 0) {
                    if (p.y >= (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y + actionRatio)
                            && p.y < arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y)
                        return true;
                } else {
                    if (p.y > arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y
                            && p.y <= (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y + actionRatio))
                        return true;
                }

            }
        }
        return false;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }

    @GetMapping("/")
    public String index() {
        return "Let the battle begin!";
    }

    @PostMapping("/**")
    public String index(@RequestBody ArenaUpdate arenaUpdate) {
        return Algorithm.calculate(arenaUpdate);

    }

    static class Self {
        public String href;
    }

    static class Links {
        public Self self;
    }

    static class PlayerState {
        public Integer x;
        public Integer y;
        public String direction;
        public Boolean wasHit;
        public Integer score;
    }

    static class Arena {
        public List<Integer> dims;
        public Map<String, PlayerState> state;
    }

    static class ArenaUpdate {
        public Links _links;
        public Arena arena;
    }

    static class Algorithm {
        public static String calculate(BridgeApplication.ArenaUpdate arenaUpdate) {
            List<String> commands = new ArrayList<>();
            int totalX = arenaUpdate.arena.dims.get(0);
            int totalY = arenaUpdate.arena.dims.get(1);
            // Avid humillation (If i am in a cornet, I am dead)
            boolean fight = fightPossible(arenaUpdate);
            boolean humillated = iAmBeingHumillated(arenaUpdate);
            boolean iCanRun = iCanRun(arenaUpdate);
            boolean iWasHit = iWasHit(arenaUpdate);
            if (iWasHit && iCanRun) {
                return run(arenaUpdate);
            } else if (fight) {
                return "T";
            } else if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x < totalX
                    && arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y < totalY
                    && arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x > 0
                    && arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y > 0) {
                // Center
                commands.add("F");
                commands.add("L");
                commands.add("R");

            } else if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x == 0) {
                // Left
                if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y == 0) {
                    // Top Left
                    if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("W") ||
                            arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("S")) {
                        commands.add("L");
                    } else if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("N") ||
                            arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("E")) {
                        commands.add("R");
                    }
                } else if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y == totalY) {
                    // Bottom Left
                    if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("W") ||
                            arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("N")) {
                        commands.add("R");
                    } else if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("S") ||
                            arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("E")) {
                        commands.add("L");
                    }
                } else {
                    // Left
                    switch (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction) {
                        case "W":
                        case "E":
                            commands.add("L");
                            commands.add("R");
                            break;
                        case "S":
                            commands.add("F");
                            commands.add("L");
                            break;
                        case "N":
                            commands.add("F");
                            commands.add("R");
                            break;
                    }
                }

            } else if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x == totalX) {
                // Right
                if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y == 0) {
                    // Top Right
                    if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("E") ||
                            arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("S")) {
                        commands.add("R");
                    } else if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("N") ||
                            arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("W")) {
                        commands.add("L");
                    }
                } else if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y.equals(arenaUpdate.arena.dims.get(1))) {
                    // Bottom Right
                    if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("E") ||
                            arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("N")) {
                        commands.add("L");
                    } else if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("S") ||
                            arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction.equals("W")) {
                        commands.add("R");
                    }
                } else {
                    // Right
                    switch (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction) {
                        case "W":
                        case "E":
                            commands.add("L");
                            commands.add("R");
                            break;
                        case "S":
                            commands.add("F");
                            commands.add("R");
                            break;
                        case "N":
                            commands.add("F");
                            commands.add("L");
                            break;
                    }
                }

            } else if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y == 0) {
                // Top
                switch (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction) {
                    case "N":
                    case "S":
                        commands.add("L");
                        commands.add("R");
                        break;
                    case "W":
                        commands.add("L");
                        commands.add("F");
                        break;
                    case "E":
                        commands.add("R");
                        commands.add("F");
                        break;
                }

            } else if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y == totalY) {
//			Bottom
                switch (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction) {
                    case "N":
                    case "S":
                        commands.add("L");
                        commands.add("R");
                        break;
                    case "W":
                        commands.add("R");
                        commands.add("F");
                        break;
                    case "E":
                        commands.add("L");
                        commands.add("F");
                        break;
                }
            }

            int i = new Random().nextInt(commands.size());
            return commands.get(i);
        }

        private static boolean iWasHit(ArenaUpdate arenaUpdate) {
            return arenaUpdate.arena.state.get(arenaUpdate._links.self.href).wasHit;
        }

        private static String run(ArenaUpdate arenaUpdate) {
            PlayerState me = arenaUpdate.arena.state.get(arenaUpdate._links.self.href);
            switch (me.direction) {
                case "N":
                    if (isEmpty(arenaUpdate, me.x, me.y + 1)) {
                        return "F";
                    } else if (isEmpty(arenaUpdate, me.x + 1, me.y)) {
                        return "R";
                    } else if (isEmpty(arenaUpdate, me.x - 1, me.y)) {
                        return "L";
                    }
                    break;
                case "S":
                    if (isEmpty(arenaUpdate, me.x, me.y - 1)) {
                        return "F";
                    } else if (isEmpty(arenaUpdate, me.x - 1, me.y)) {
                        return "R";
                    } else if (isEmpty(arenaUpdate, me.x + 1, me.y)) {
                        return "L";
                    }
                    break;
                case "W":
                    if (isEmpty(arenaUpdate, me.x - 1, me.y)) {
                        return "F";
                    } else if (isEmpty(arenaUpdate, me.x, me.y + 1)) {
                        return "R";
                    } else if (isEmpty(arenaUpdate, me.x, me.y - 1)) {
                        return "L";
                    }
                    break;
                case "E":
                    if (isEmpty(arenaUpdate, me.x + 1, me.y)) {
                        return "F";
                    } else if (isEmpty(arenaUpdate, me.x, me.y - 1)) {
                        return "R";
                    } else if (isEmpty(arenaUpdate, me.x, me.y + 1)) {
                        return "L";
                    }
                    break;
            }

            return "T";
        }

        private static boolean iCanRun(ArenaUpdate arenaUpdate) {
            int maxX = arenaUpdate.arena.dims.get(0);
            int maxY = arenaUpdate.arena.dims.get(1);
            PlayerState me = arenaUpdate.arena.state.get(arenaUpdate._links.self.href);
            return isEmpty(arenaUpdate, me.x, me.y - 1)
                    || isEmpty(arenaUpdate, me.x, me.y + 1)
                    || isEmpty(arenaUpdate, me.x - 1, me.y)
                    || isEmpty(arenaUpdate, me.x + 1, me.y);
        }

        private static boolean isEmpty(ArenaUpdate arenaUpdate, int x, int y) {
            if (x > arenaUpdate.arena.dims.get(0) || y > arenaUpdate.arena.dims.get(1)) {
                return false;
            }
            return arenaUpdate.arena.state.entrySet().stream()
                    .filter(e -> e.getValue().x == x && e.getValue().y == y)
                    .findFirst().isEmpty();
        }
    }
}
