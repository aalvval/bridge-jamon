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
            commands.add("T");
            int totalX = arenaUpdate.arena.dims.get(0);
            int totalY = arenaUpdate.arena.dims.get(1);

            if (fightPossible(arenaUpdate)) {
                return "T";
            } else if (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x < totalX
                    && arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y < totalY) {
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

        private static boolean fightPossible(BridgeApplication.ArenaUpdate arenaUpdate) {
            return false;
//            int actionRatio = 0;
//            int direction = 0;
//            switch (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction) {
//                case "N":
//                    direction = 1;
//                    actionRatio = -3;
//                    break;
//                case "W":
//                    direction = 0;
//                    actionRatio = -3;
//                    break;
//                case "E":
//                    direction = 0;
//                    actionRatio = 3;
//                    break;
//                case "S":
//                    direction = 1;
//                    actionRatio = 3;
//
//            }
//            if (direction == 1) {
//                return checkEnemiesDirectionY(arenaUpdate, actionRatio);
//            } else {
//                return checkEnemiesDirectionX(arenaUpdate, actionRatio);
//            }

        }

        private static boolean checkEnemiesDirectionX(BridgeApplication.ArenaUpdate arenaUpdate, int actionRatio) {
            Iterator<Map.Entry<String, BridgeApplication.PlayerState>> iterator = arenaUpdate.arena.state.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, BridgeApplication.PlayerState> e = iterator.next();
                String s = e.getKey();
                BridgeApplication.PlayerState p = e.getValue();
                if (!s.equals(arenaUpdate._links.self.href)) {
                    if (actionRatio < 0) {
                        if (p.x > arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x + actionRatio
                                && p.x < arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x)
                            return true;
                    } else {
                        if (p.x > arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x
                                && p.x < arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x + actionRatio)
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
                if (!s.equals(arenaUpdate._links.self.href)) {
                    if (actionRatio < 0) {
                        if (p.y > arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y + actionRatio
                                && p.y < arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y)
                            return true;
                    } else {
                        if (p.y > arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y
                                && p.y < arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y + actionRatio)
                            return true;
                    }

                }
            }
            return false;
        }
    }

}
