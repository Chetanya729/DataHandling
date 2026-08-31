//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() throws FileNotFoundException {
        List<String> newList1 = Arrays.asList("Tarun", "Chirag", "Messi", "Ronaldo");
        List<Integer> newList2 = Arrays.asList(12,123,122,3323,332,11,224,4556,334,776);
        List<Integer> evenNumbers = new ArrayList<>();
        List<String> names = new ArrayList<>();
        evenNumbers= newList2.stream().filter(n->n%2==0).collect(Collectors.toList());
        System.out.println(evenNumbers);
        names = newList1.stream().map(n->n.toLowerCase()).collect(Collectors.toList());
        System.out.println(names);
        List<String> newList3 = Arrays.asList("Lovepreet", "Naman", "Rahul", "Addaa");
        List<String> newList4 = Arrays.asList("Oman", "Lucky", "Manan", "Preet");
        List<List<String>> finalList = Arrays.asList(newList1,newList3,newList4);
        List<String>finallist = finalList.stream().flatMap(l->l.stream()).collect(Collectors.toList());
        System.out.println(finallist);
        List<Integer> ordered = newList2.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        System.out.println(ordered);
        Optional<List> combinedList = Arrays.stream(finalList.toArray(new List[0])).reduce((a, b ) -> Collections.singletonList(a + "--" + b ));
        combinedList.ifPresent(System.out::println);
        Boolean result = newList1.stream().anyMatch(n->n.equals("Tarun"));
        System.out.println(result);
        Optional<String> findany = newList1.stream().findAny();
        findany.ifPresent(System.out::println);
        findany =  newList1.stream().findFirst();
        findany.ifPresent(System.out::println);
        finallist = Stream.concat(newList1.stream(),newList3.stream()).collect(Collectors.toList());
        for (String list : finallist) {
                System.out.println(list);
        }
        Scanner in = new Scanner(System.in);
        int MIN_AGE = in.nextInt();
        int MAX_AGE = in.nextInt();

        Path csv = Path.of("src/WA_Fn-UseC_-HR-Employee-Attrition.xls");
        if (!Files.exists(csv)) {
                System.err.println("CSV not found at " + csv.toAbsolutePath()
                                 + "  (run with the project folder as the working directory)");
                return;
        }

        List<Employee> employees;
        try (BufferedReader br = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
                String headerLine = br.readLine();
                if (headerLine == null) throw new IOException("Empty file: " + csv);
                if (headerLine.charAt(0) == '\uFEFF') headerLine = headerLine.substring(1);  // the BOM

                String[] headers = headerLine.split(",", -1);
                Map<String, Integer> idx = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                        idx.put(headers[i].trim(), i);
                }
                employees = br.lines()                             // BufferedReader -> Stream
                        .filter(line -> !line.isBlank())
                        .map(line -> line.split(",", -1))
                        .map(cells -> toEmployee(cells, idx))
                        .toList();
        } catch (IOException e) {
                throw new RuntimeException(e);
        }

        List<Employee> inRange = employees.stream()
                .filter(e -> e.age() >= MIN_AGE && e.age() <= MAX_AGE )
                .filter(e->e.gender().startsWith("M"))
                .sorted(Comparator.comparingInt(Employee::age))
                .toList();

        System.out.printf("%n=== Employees aged %d to %d ===%n%n", MIN_AGE, MAX_AGE);
        System.out.printf("%-4s %-24s %-27s %10s  %-9s %10s %n", "Age", "Department", "JobRole", "Income", "Attrition","Gender");
        System.out.println("-".repeat(88));
        inRange.forEach(e -> System.out.printf("%-4d %-24s %-27s %,10d %9s %10s %n",
                e.age(), e.department(), e.jobRole(), e.monthlyIncome(), e.attrition(), e.gender()));
        System.out.printf("%n%d of %d employees matched.%n", inRange.size(), employees.size());
}

record Employee(int age, String department, String jobRole, int monthlyIncome, String attrition,String gender) {}

Employee toEmployee(String[] cells, Map<String, Integer> idx) {
        return new Employee(
                Integer.parseInt(cells[idx.get("Age")].trim()),
                cells[idx.get("Department")].trim(),
                cells[idx.get("JobRole")].trim(),
                Integer.parseInt(cells[idx.get("MonthlyIncome")].trim()),
                cells[idx.get("Attrition")].trim(),
                cells[idx.get("Gender")].trim()
        );
}
