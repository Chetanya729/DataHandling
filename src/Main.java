//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import java.util.Collections;

void main() {
        List<Employee> employees = loadEmployees();
//        basicspractice();
        matchingAndFinding(employees);
        primitiveStreams(employees);
}

private List<Employee> loadEmployees() {
        Path csv = Path.of("src/WA_Fn-UseC_-HR-Employee-Attrition.xls");
        if (!Files.exists(csv)) {
                System.err.println("CSV not found at " + csv.toAbsolutePath()
                        + "  (run with the project folder as the working directory)");
                return Collections.emptyList();
        }

        try (BufferedReader br = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
                String headerLine = br.readLine();
                if (headerLine == null) throw new IOException("Empty file: " + csv);
                if (headerLine.charAt(0) == '\uFEFF') headerLine = headerLine.substring(1);

                String[] headers = headerLine.split(",", -1);
                Map<String, Integer> idx = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                        idx.put(headers[i].trim(), i);
                }
               return br.lines()
                        .filter(line -> !line.isBlank())
                        .map(line -> line.split(",", -1))
                        .map(cells -> toEmployee(cells, idx))
                        .toList();
        } catch (IOException e) {
                throw new RuntimeException(e);
        }
}

void basicspractice(){
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
}

void matchingAndFinding(List<Employee> employees){
        System.out.println("\n Choose the option you want to continue with:\n 1.List All Employees\n 2.Show the list of employee between the age range\n 3.Show the list of employee between the salary range\n 4.How many employees work in sales?\n 5.What job roles exst? What departments\n 6.The top 10 highest earners\n 7.Ranks 11-20 by income\n 8.Youngest and oldest employee\n 9.Is every employee over 18?\n 10.Is there nobody earning under 1000?\n 11.Exit");
        Scanner in = new Scanner(System.in);
        int option = in.nextInt();
        switch (option){
                case 1:
                       List<Employee> allEmployees =  employees.stream().sorted(Comparator.comparing(Employee::age)).toList();
                       printEmployees(allEmployees);
                       break;
                case 2:
                        System.out.println("Enter the min age");
                        int MIN_AGE = in.nextInt();
                        System.out.println("Enter the max age");
                        int MAX_AGE = in.nextInt();
                        List<Employee> inRange = employees.stream()
                                .filter(e -> e.age() >= MIN_AGE && e.age() <= MAX_AGE )
                                .filter(e->e.gender().startsWith("M"))
                                .sorted(Comparator.comparingInt(Employee::age))
                                .toList();
                                printEmployees(inRange);
                        System.out.printf("%n%d of %d employees matched.%n", inRange.size(), employees.size());
                        break;
                case 3:
                        System.out.println("Enter the min salary range");
                        int MIN_SALARY_RANGE = in.nextInt();
                        System.out.println("Enter the max salary range");
                        int MAX_SALARY_RANGE = in.nextInt();
                        List<Employee> inRangeSalary = employees.stream()
                                .filter(e->e.monthlyIncome()>=MIN_SALARY_RANGE && e.monthlyIncome <= MAX_SALARY_RANGE)
                                .sorted(Comparator.comparingInt(Employee::monthlyIncome)).toList();
                        printEmployees(inRangeSalary);
                        System.out.printf("%n%d of %d employees matched.%n", inRangeSalary.size(), employees.size());
                        break;
                case 4:
                        long salesCount = employees.stream().filter(e->e.department.equals("Sales")).count();
                        System.out.println("Total employess in sales department: " + salesCount);
                        break;
                case 5:
                        employees.stream()
                                .map(Employee::jobRole)
                                .distinct()
                                .sorted()
                                .forEach(System.out::println);
                        break;
                case 6: employees.stream()
                                .map(Employee::monthlyIncome)
                                .sorted(Comparator.reverseOrder())
                                .limit(10)
                                .forEach(System.out::println);
                        break;
                case 7:
                        List<Employee> employeeBYIncome= employees.stream()
                                .sorted(Comparator.comparing(Employee::monthlyIncome).reversed())
                                .skip(10)
                                .limit(10)
                                .toList();
                        printEmployees(employeeBYIncome);
                        System.out.printf("%n%d of %d employees matched.%n", employeeBYIncome.size(), employees.size());
                        break;
                case 8:
                        List<Employee> youngestAndOldest =  employees.stream()
                                        .collect(Collectors.teeing(
                                                Collectors.minBy(Comparator.comparingInt(Employee::age).thenComparing(Employee::monthlyIncome)),
                                                Collectors.maxBy(Comparator.comparingInt(Employee::age).thenComparing(Employee::monthlyIncome)),
                                                (min,max)-> Stream.of(min,max)
                                                        .flatMap(Optional::stream).toList()));

                      printEmployees(youngestAndOldest);
                        break;
                case 9:
                       boolean employeesLessThan18 = employees.stream()
                                .anyMatch(e->e.age()<18);
                        System.out.println("Is any employee below the age of 18?"+"\n"+employeesLessThan18);
                        break;
                case 10:
                        boolean earningLessthan1000 = employees.stream().noneMatch(e->e.monthlyIncome()>=1000);
                        System.out.println("Is there anybody earning less than 1000"+"\n"+earningLessthan1000);
                        break;
                default:
                        System.out.println("Invalid option");
        }
}

record Employee(int age, String department, String jobRole, int monthlyIncome, String attrition,String gender,String Education_Field,String BusinessTravel) {}

Employee toEmployee(String[] cells, Map<String, Integer> idx) {
        return new Employee(
                Integer.parseInt(cells[idx.get("Age")].trim()),
                cells[idx.get("Department")].trim(),
                cells[idx.get("JobRole")].trim(),
                Integer.parseInt(cells[idx.get("MonthlyIncome")].trim()),
                cells[idx.get("Attrition")].trim(),
                cells[idx.get("Gender")].trim(),
                cells[idx.get("EducationField")].trim(),
                cells[idx.get("BusinessTravel")].trim()
        );
}
void printEmployees(List<Employee> rows) {
        System.out.printf("%-4s %-24s %-27s %10s  %-9s %10s %25s %30s %n",
                "Age", "Department", "JobRole", "Income", "Attrition", "Gender",
                "EducationField", "BusinessTravel");
        System.out.println("-".repeat(130));
        rows.forEach(e -> System.out.printf("%-4d %-24s %-27s %10d %9s %10s %25s %30s %n",
                e.age(), e.department(), e.jobRole(), e.monthlyIncome(), e.attrition(),
                e.gender(), e.Education_Field(), e.BusinessTravel()));
        System.out.printf("%n%d employees listed.%n", rows.size());
}
void primitiveStreams(List<Employee> employees){
        System.out.println("New List of Data:\n 1.Total monthly payroll\n 2.Average age\n 3.Income Statistics\n 4.How many employees per department\n 5.Head count per job role\n 6.Average income per department\n 7.Total payroll per department\n 8.Count of gender in particular department\n");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        switch (choice) {
                case 1:
                        long totalMonthlyPayroll = employees.stream().mapToInt(Employee::monthlyIncome).sum();
                        System.out.println("Total monthly payroll is: " + totalMonthlyPayroll);
                        break;
                case 2:
                        OptionalDouble averageAge = employees.stream().mapToInt(Employee::age).average();
                        averageAge.ifPresent(System.out::println);
                        break;
                case 3:
                        IntSummaryStatistics statistics = employees.stream().mapToInt(Employee::monthlyIncome).summaryStatistics();
                        System.out.println("Average monthly payroll is: " + statistics);
                        break;
                case 4:
                        Map<String,Long> employeesInDepartment = employees.stream().collect(Collectors.groupingBy(Employee::department, Collectors.counting()));
                        System.out.println("Employees according to department\n"+employeesInDepartment);
                        break;
                case 5:
                        Map<String,Long> accordingTojobRole = employees.stream().collect(Collectors.groupingBy(Employee::jobRole, Collectors.counting()));
                        System.out.println("Employees according to job role\n"+accordingTojobRole);
                        break;
                case 6:
                        Map<String,Double> averageAccordingToDepartment = employees.stream().collect(Collectors.groupingBy(Employee::department, Collectors.averagingDouble(Employee::monthlyIncome)));
                        System.out.println("Average income according to department\n"+averageAccordingToDepartment);
                        break;
                case 7:
                        Map<String ,Integer> totalPayDepartment = employees.stream().collect(Collectors.groupingBy(Employee::department, Collectors.summingInt(Employee::monthlyIncome)));
                        System.out.println("Total pay department\n"+totalPayDepartment);
                        break;
                case 8:
                        Map<String, Map<String, Long>> departmentGender = employees.stream().collect(Collectors.groupingBy(Employee::department,Collectors.groupingBy(Employee::gender,Collectors.counting())));
                        System.out.println("Gender count per department\n"+departmentGender);
                        break;
                        default:
                                System.out.println("Invalid option");


        }
}