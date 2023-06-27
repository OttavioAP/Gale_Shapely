/*
 * Name: <your name>
 * EID: <your EID>
 */

import java.util.ArrayList;
import java.util.Collections;

/**
 * Your solution goes in this class.
 *
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 *
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {


    /**
     * Determines whether a candidate Matching represents a solution to the stable matching problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
    /*
        * Check for type one instability
    For each high school h 
        For each student s’ that is unmatched
        Check if the high school would rather have s’ than one of their current students
        if so, declare unstable

        Check for type two instability

    For each high school h 
        For each student s’ that’s already matched
            Check if the high school would rather have s’ than one of their current students
                If so, check if the student would rather have h than h’, their current high school
                    if so, declare unstable
            otherwise do nothing
        


     * 
     */

    @Override
public boolean isStableMatching(Matching problem) {
    int m = problem.getHighSchoolCount();
    int n = problem.getStudentCount();

    ArrayList<ArrayList<Integer>> highschoolPrefs = problem.getHighSchoolPreference();//indexed by high school, contains an arraylist of their student pref, indexed by student
    ArrayList<ArrayList<Integer>> studentPrefs = problem.getStudentPreference();//indexed by student, contains an arraylist of their high school pref, indexed by high school
    ArrayList<Integer> currMatching = problem.getStudentMatching();
    ArrayList<Integer> highschoolSpots = problem.getHighSchoolSpots();

for(int sp =0;sp<n;sp++){//For each student sp

        for(int hp =0; hp < m;hp++){//for each high school to consider


            int leastPreferredStudent = -1;
            int leastPreferredRank = -1;
            int studentsInSchool = 0;

            for(int s =0; s < n;s++){//find every student that belongs to that school
                        if(currMatching.get(s) == hp){ //check for belonging to that school
                        studentsInSchool++;
                            if(leastPreferredRank < highschoolPrefs.get(hp).indexOf(s)){ //compare to find least desirable student
                                //drop s, add studentprime  
                                leastPreferredRank = highschoolPrefs.get(hp).indexOf(s);
                                leastPreferredStudent = s;
                            }
                        }
                    }
            
            int schoolSpots = highschoolSpots.get(hp);
            if(studentsInSchool < schoolSpots){ //something has gone horribly wrong if there are any high schools without all their spots filled
                System.out.println("High School Has Empty Spot");
                return false;
            }

            int s = leastPreferredStudent;
            if(compareStudents(sp, hp, s, highschoolPrefs)){//if sp is more desirable than s
                if(currMatching.get(sp)==-1){//If student is unmatched (check for type 1)
                    System.out.println("type one instability");
                    return false; //then it's unstable
                }else{//if student is matched, check for type 2
                    int h = currMatching.get(s); //h is sps current school
                    if(compareSchools(hp, h, s, studentPrefs)){//if hp is more desirable than h
                        System.out.println("type two instability");
                        return false; 
                    }
                }
            }
    }
}
    return true;
}

//returns true if hp is more desirable than h
private boolean compareSchools(int hp,int h, int s, ArrayList<ArrayList<Integer>> studentPreferences){
    if(studentPreferences.get(s).indexOf(hp) < studentPreferences.get(s).indexOf(h)){//if index of hp is lower than index of h, hp is more desirable
        return true;
    }
    return false;
}

//returns true if sp is more desirable than s
private boolean compareStudents(int sp,int h, int s, ArrayList<ArrayList<Integer>> schoolPreferences){
    if(schoolPreferences.get(h).indexOf(sp) < schoolPreferences.get(h).indexOf(s)){//if index of sp is lower than index of s, sp is more desirable
        return true;
    }
    return false;
}




public class StudentRank {
    public int student;
    public int rank;

    public StudentRank(int student, int rank) {
        this.student = student;
        this.rank = rank;
    }
}


        /**

     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_studentoptimal(Matching problem) {
        int m = problem.getHighSchoolCount();
        int n = problem.getStudentCount();
        ArrayList<ArrayList<Integer>> highschoolPrefs = problem.getHighSchoolPreference();
        ArrayList<ArrayList<Integer>> studentPrefs = problem.getStudentPreference();
        ArrayList<Integer> highschoolSpots = new ArrayList<>();
        highschoolSpots.addAll(problem.getHighSchoolSpots());//indexed by high schools, contains there # of spots

        ArrayList<Integer> studentMatching = new ArrayList<>(Collections.nCopies(n, -1)); // Initialize all students as unmatched
        ArrayList<Integer> studentPreferenceIndex = new ArrayList<>(Collections.nCopies(n, 0)); // Initialize all students as unmatched

        boolean studentRemains = true;
        int studentPrime = -1;
        while(studentRemains){ //while a student hasn't reached the end of their preferencelist. # of elements in preference lists = m * n

            studentRemains = false;

            for(int sp =0;sp < n;sp++){ //search for an unmatched student that hasn't reached the end of their preference list
                if(studentPreferenceIndex.get(sp) < m) //check that hasn't reached end of preference list 
                if(studentMatching.get(sp) == -1){ //if that student is unmatched
                    //have that student propose to the next school on their list
                    studentRemains = true;
                    studentPrime = sp;
                    break;
                }
            }

            if(studentRemains){
                int h = studentPrefs.get(studentPrime).get(studentPreferenceIndex.get(studentPrime)); //gets next desired school
                studentPreferenceIndex.set(studentPrime, studentPreferenceIndex.get(studentPrime) +1); //increment after use

                if(highschoolSpots.get(h) > 0 ){//check if the school has an open spot
                    highschoolSpots.set(h,highschoolSpots.get(h) - 1); //decrement number of spots
                    studentMatching.set(studentPrime, h); //match the two
                    
                }else{ //check if the high school would rather have them or their least desirable student

                    int leastPreferredRank =-1;
                    int leastPreferredStudent = -1;

                    for(int s =0; s < n;s++){//find every student that belongs to that school
                        if(studentMatching.get(s) == h){ //check for belonging to that school

                            if(leastPreferredRank < highschoolPrefs.get(h).indexOf(s)){ //compare to find least desirable student
                                //drop s, add studentprime  
                                leastPreferredRank = highschoolPrefs.get(h).indexOf(s);
                                leastPreferredStudent = s;
                            }
                        }
                    }

                    if(leastPreferredRank > 0){ //if the least desirable student is less desirable than studentprime, swap
                        if(leastPreferredRank >  highschoolPrefs.get(h).indexOf(studentPrime)){
                        studentMatching.set(studentPrime, h);
                        studentMatching.set(leastPreferredStudent, -1);
                        }
                    }
                }
        }
            }

            problem.setStudentMatching(studentMatching);
            return problem;
    }

























    /**
     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_highschooloptimal(Matching problem) {
        int m = problem.getHighSchoolCount();
        int n = problem.getStudentCount();
        int totalSpots = problem.totalHighSchoolSpots();

        ArrayList<ArrayList<Integer>> highschoolPrefs = problem.getHighSchoolPreference();//indexed by high school, contains an arraylist of their student pref, indexed by student
        ArrayList<ArrayList<Integer>> studentPrefs = problem.getStudentPreference();//indexed by student, contains an arraylist of their high school pref, indexed by high school
        ArrayList<Integer> highschoolSpots = new ArrayList<>();
        highschoolSpots.addAll(problem.getHighSchoolSpots());//indexed by high schools, contains there # of spots
        ArrayList<Integer> schoolPreferenceIndex = new ArrayList<>(Collections.nCopies(m, 0)); // Initialize all students as unmatched
        ArrayList<Integer> studentMatching = new ArrayList<>(Collections.nCopies(n, -1)); // Initialize all students as unmatched

        while(totalSpots >0){ //go until no spots remain for any school

            int currentSchool = -1;
            for(int h =0; h < m; h++){ // find a high school that still has spots
                if(highschoolSpots.get(h) > 0){
                    currentSchool = h;
                    break;
                }
            }

            int studentPrime = highschoolPrefs.get(currentSchool).get(schoolPreferenceIndex.get(currentSchool)); //find the next desired student for that high school
            schoolPreferenceIndex.set(currentSchool,schoolPreferenceIndex.get(currentSchool) + 1); //need to increment index 

            if(studentMatching.get(studentPrime) == -1){//if available, matches immediately
                highschoolSpots.set(currentSchool,highschoolSpots.get(currentSchool) -1); //decrement number of spots for that hs
                totalSpots --; //decrement total number of spots
            }else{ 

                //check if that student would prefer the high school to the one they're currently matched to
                int AlreadyMatchedSchool = studentMatching.get(studentPrime);
                if( studentPrefs.get(studentPrime).indexOf(currentSchool) < studentPrefs.get(studentPrime).indexOf(AlreadyMatchedSchool)){
                    highschoolSpots.set(AlreadyMatchedSchool,highschoolSpots.get(AlreadyMatchedSchool) +1); //open up spot at sniped hs
                    highschoolSpots.set(currentSchool,highschoolSpots.get(currentSchool) -1); //decrement number of spots for current hs
                    studentMatching.set(studentPrime,currentSchool); //actually perform matching
                }

            }
            


        }

        

         problem.setStudentMatching(studentMatching);
            return problem;
    }









    // /**
    //  * Determines a solution to the stable matching problem from the given input set. Study the
    //  * project description to understand the variables which represent the input to your solution.
    //  *
    //  * @return A stable Matching.
    //  */
    // @Override
    // public Matching stableMatchingGaleShapley_highschooloptimal(Matching problem) {
    //     /*
    //      * get data
    //      * create while loop for total spots
    //      * create 2d arraylist of high school spots
    //      * create arraylist of indices for high school spots 
    //      * create 
    //      * 
    //      * while spots remaining
    //      *      find a high school with remaining spots
    //      *      have it propose to its next highest preference student
    //      *      if the student is single, it accepts
    //      *      if the student is not single, use the students preference list to check if the high school proposing is higher or lower on his pref list
    //      *      if it's lower, do nothing
    //      *      if its higher, then remove that student from the arraylist of hs spots, and decrease the index of that high school
    //      *      then, update the Matching arraylist (constant)
    //      *      Then, update the corresponding high schools arraylist (constant)
    //      * 
    //      */

    //     int m = problem.getHighSchoolCount();
    //     int n = problem.getStudentCount();
    //     int totalSpots = problem.totalHighSchoolSpots();
    //     int currentlyTakenSpots =0;
    //     ArrayList<ArrayList<Integer>> highschoolPrefs = problem.getHighSchoolPreference();//indexed by high school, contains an arraylist of their student pref, indexed by student
    //     ArrayList<ArrayList<Integer>> studentPrefs = problem.getStudentPreference();//indexed by student, contains an arraylist of their high school pref, indexed by high school
    //     ArrayList<Integer> highschoolSpots = problem.getHighSchoolSpots();//indexed by high schools, contains there # of spots
        

    //     ArrayList<LinkedList<Integer>> proposals = new ArrayList<>(m); // Track the proposals made by each high school to students, indexed by high school
    //     for (int i = 0; i < m; i++) {
    //         proposals.add(new LinkedList<>());
    //     }

    //     ArrayList<Integer> proposalsIndex = new ArrayList<>(m);//index of proposals, tracks length of proposals List
    //     for (int i = 0; i < m; i++) {
    //         proposalsIndex.add(0);
    //     }
    //     ArrayList<Integer> nextPrefStudent = new ArrayList<>(m);//holds the index of the next student to propose to for each hs, indexed by hs
    //     for (int i = 0; i < m; i++) {
    //         nextPrefStudent.add(0);
    //     }

    //     ArrayList<Integer> matching= new ArrayList<>(n); // Initialize all students as unmatched
    //     for (int i = 0; i < n; i++) {
    //         matching.add(-1);
    //     }


    //     while (currentlyTakenSpots <totalSpots){
    //         int currentHighSchool = -1;
    //         int currentStudent = -1;
    //         //find a high school i with available spots

    //         for(int i =0; i < m;i++){
    //             if(proposalsIndex.get(i) < highschoolSpots.get(i)){ //check if has open spot
    //                 currentHighSchool = i;
    //                 break;
    //             }
    //         }
    //         if (currentHighSchool == -1) {
    //             throw new RuntimeException("Error: No open spots found");
    //         }

    //         //find next student
    //         currentStudent = highschoolPrefs.get(currentHighSchool).get(nextPrefStudent.get(currentHighSchool));
    //         nextPrefStudent.set(currentHighSchool,currentStudent + 1); //increment index

    //         //propose
    //         if(matching.get(currentStudent) ==-1){
    //             //match up, increment everything, decrease total spots
    //             currentlyTakenSpots++;
    //             matching.set(currentStudent,currentHighSchool);
    //             proposals.get(currentHighSchool).add(currentStudent);
    //             proposalsIndex.set(currentHighSchool,(proposalsIndex.get(currentHighSchool) +1 )); //increment index
            
    //         }else{
    //             //compare,maybe match up, maybe do nothing. If do nothing, still increment 
    //             //compare
    //             int compareHighSchool = matching.get(currentStudent);
    //             int compPref = studentPrefs.indexOf(compareHighSchool); //o(n) operation
    //             int currPreference = studentPrefs.indexOf(currentHighSchool);

    //             if(currPreference < compPref){ //lower index means higher preference, means switch
    //                 //switch
    //                 matching.set(currentStudent, currentHighSchool);//perform actual switch
    //                 proposals.get(compareHighSchool).remove(currentStudent);
    //                 proposalsIndex.set(compareHighSchool,proposalsIndex.get(compareHighSchool) -1);

    //                 proposals.get(currentHighSchool).add(currentStudent); //switch
    //                 proposalsIndex.set(currentHighSchool,proposalsIndex.get(currentHighSchool) +1);

    //             }
    //             //if index is higher, then it's lower preference, don't switch
    //         }
            
    //     } 

    //     return new Matching(problem, matching);

    // }
    
    
}

/*
 * matching object includes
 * m number of high schools
 * n number of students
 * highschool rpeference list (arraylist of arraylist)
 * stdeunt preference 
 * high school spots
 * student matching
 * 
 * 
 */